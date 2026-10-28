package com.hospital.lab.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.lab.repository.LaboratoryRepository;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LaboratoryService extends DomainServiceSupport {
  private final LaboratoryRepository repository;

  public LaboratoryService(
      LaboratoryRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','LAB_TECHNICIAN')")
  public Object list(int page, Jwt jwt) {
    if (com.hospital.common.security.Actor.has(jwt, "DOCTOR"))
      return repository.listForPatients("order", page, 20, "", directory.assignedPatients());
    return repository.list("order", page, 20, "", "");
  }

  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
  public Object order(Map<String, Object> b) {
    directory.care(UUID.fromString(Validation.required(b, "patientId")));
    Validation.required(b, "test");
    var tests = repository.findTest(Validation.required(b, "test"));
    Validation.require(!tests.isEmpty(), "Choose an active catalogue test");
    b.put("referenceRange", tests.getFirst().get("reference_range"));
    b.put("unit", tests.getFirst().get("unit"));
    b.put("status", "ORDERED");
    return create("order", b, "lab.order-created.v1");
  }

  @PreAuthorize("hasRole('LAB_TECHNICIAN')")
  @Transactional
  public Object transition(UUID id, Map<String, Object> b, Jwt jwt) {
    var r = repository.get("order", id);
    String next = Validation.required(b, "status");
    String current = r.get("status").toString();
    Validation.require(
        Map.of(
                "ORDERED",
                "COLLECTED",
                "COLLECTED",
                "PROCESSING",
                "PROCESSING",
                "RESULT_ENTERED",
                "RESULT_ENTERED",
                "VERIFIED")
            .getOrDefault(current, "")
            .equals(next),
        "Illegal laboratory transition");
    if (next.equals("COLLECTED")) repository.sample(id, UUID.fromString(jwt.getSubject()));
    if (next.equals("RESULT_ENTERED")) {
      r.put("result", Validation.required(b, "result"));
      r.put("enteredBy", jwt.getSubject());
    }
    if (next.equals("VERIFIED"))
      Validation.require(
          !jwt.getSubject().equals(r.get("enteredBy")), "A second technician must verify results");
    if (next.equals("VERIFIED")) r.put("verifiedBy", jwt.getSubject());
    r.put("status", next);
    long version = ((Number) r.remove("version")).longValue();
    r.remove("id");
    r.remove("createdAt");
    repository.transitionHistory(id, current, next, UUID.fromString(jwt.getSubject()));
    audit.record(jwt.getSubject(), "LAB_" + next, id);
    return update(
        "order", id, r, version, next.equals("VERIFIED") ? "lab.result-verified.v1" : null);
  }

  public Object history(UUID id, Jwt jwt) {
    var row = repository.get("order", id);
    if (com.hospital.common.security.Actor.has(jwt, "DOCTOR"))
      directory.care(UUID.fromString(row.get("patientId").toString()));
    return repository.orderHistory(id);
  }
}
