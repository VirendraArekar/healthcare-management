package com.hospital.medicalrecord.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.medicalrecord.repository.MedicalRecordRepository;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MedicalRecordService extends DomainServiceSupport {
  private final MedicalRecordRepository repository;

  public MedicalRecordService(
      MedicalRecordRepository repository,
      EventPublisher events,
      AuditService audit,
      JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PATIENT')")
  public Object list(int page, int size, String q, Jwt jwt) {
    if (com.hospital.common.security.Actor.has(jwt, "DOCTOR")
        || com.hospital.common.security.Actor.has(jwt, "NURSE"))
      return repository.listForPatients("encounter", page, size, q, directory.assignedPatients());
    return repository.list("encounter", page, size, q, owner(jwt));
  }

  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PATIENT')")
  public Object get(UUID id, Jwt jwt) {
    var r = repository.get("encounter", id);
    if (!com.hospital.common.security.Actor.has(jwt, "PATIENT"))
      directory.care(UUID.fromString(r.get("patientId").toString()));
    if (!owner(jwt).isEmpty()
        && !owner(jwt).equals(Objects.toString(r.get("patientId"), r.get("id").toString())))
      throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
    audit.record(jwt.getSubject(), "READ_encounter", id);
    return r;
  }

  @PreAuthorize("hasAnyRole('DOCTOR')")
  @Transactional
  public Object create(Map<String, Object> body, Jwt jwt) {
    directory.care(UUID.fromString(Validation.required(body, "patientId")));
    Validation.required(body, "diagnosis");
    Validation.required(body, "notes");
    Validation.required(body, "treatment");
    var r = create("encounter", body, "medical-record.created.v1");
    audit.record(jwt.getSubject(), "CREATE_encounter", UUID.fromString(r.get("id").toString()));
    return r;
  }

  @PreAuthorize("hasRole('DOCTOR')")
  @Transactional
  public Object update(UUID id, Map<String, Object> body, Jwt jwt) {
    var current = repository.get("encounter", id);
    directory.care(UUID.fromString(current.get("patientId").toString()));
    long version = Long.parseLong(Validation.required(body, "version"));
    body.put("patientId", current.get("patientId"));
    repository.insertRecordHistory(
        UUID.randomUUID(), id, current.get("version"), codec.json(current), jwt.getSubject());
    audit.record(jwt.getSubject(), "UPDATE_ENCOUNTER", id);
    return update("encounter", id, body, version, null);
  }

  @PreAuthorize("hasAnyRole('NURSE','DOCTOR')")
  @Transactional
  public Object vitals(UUID id, Map<String, Object> body, Jwt jwt) {
    var encounter = repository.get("encounter", id);
    directory.care(UUID.fromString(encounter.get("patientId").toString()));
    String observation = Validation.required(body, "observation");
    audit.record(jwt.getSubject(), "RECORD_VITALS", id);
    var data = new LinkedHashMap<String, Object>(body);
    data.put("encounterId", id.toString());
    data.put("recordedBy", jwt.getSubject());
    return create("vitals", data, null);
  }

  private String owner(Jwt jwt) {
    return jwt.getClaimAsStringList("roles").contains("PATIENT")
        ? Objects.requireNonNullElse(jwt.getClaimAsString("patientId"), "unbound")
        : "";
  }

  public Object history(UUID id, Jwt jwt) {
    get(id, jwt);
    return repository.history(id);
  }

  public Object observations(UUID id, Jwt jwt) {
    get(id, jwt);
    return repository.observations(id);
  }

  @PreAuthorize("hasRole('DOCTOR')")
  public Object prescribe(Map<String, Object> b, Jwt jwt) {
    UUID patient = UUID.fromString(Validation.required(b, "patientId"));
    directory.care(patient);
    Validation.required(b, "medicine");
    int qty = Integer.parseInt(Validation.required(b, "quantity"));
    Validation.require(qty > 0, "Quantity must be positive");
    LocalDate expiry = LocalDate.parse(Validation.required(b, "validUntil"));
    Validation.require(!expiry.isBefore(LocalDate.now()), "Prescription expired");
    b.put("doctorId", jwt.getSubject());
    b.put("status", "ACTIVE");
    audit.record(jwt.getSubject(), "PRESCRIBE", patient);
    return create("prescription", b, null);
  }

  @PreAuthorize("hasAnyRole('DOCTOR','PHARMACIST','PATIENT')")
  public Object prescription(UUID id, Jwt jwt) {
    var p = repository.get("prescription", id);
    if (com.hospital.common.security.Actor.has(jwt, "PATIENT"))
      Validation.require(
          com.hospital.common.security.Actor.patient(jwt).toString().equals(p.get("patientId")),
          "Prescription inaccessible");
    if (com.hospital.common.security.Actor.has(jwt, "DOCTOR"))
      directory.care(UUID.fromString(p.get("patientId").toString()));
    return p;
  }

  public Object prescriptions(int page, Jwt jwt) {
    if (com.hospital.common.security.Actor.has(jwt, "PATIENT"))
      return repository.list(
          "prescription", page, 20, "", com.hospital.common.security.Actor.patient(jwt).toString());
    if (com.hospital.common.security.Actor.has(jwt, "DOCTOR"))
      return repository.listForPatients("prescription", page, 20, "", directory.assignedPatients());
    return repository.list("prescription", page, 20, "", "");
  }

  public Object close(UUID id, Jwt jwt) {
    var record = (Map<String, Object>) get(id, jwt);
    long version = ((Number) record.remove("version")).longValue();
    Validation.require("OPEN".equals(record.get("status")), "Encounter is not open");
    record.put("status", "CLOSED");
    audit.record(jwt.getSubject(), "CLOSE_ENCOUNTER", id);
    return update("encounter", id, record, version, null);
  }
}
