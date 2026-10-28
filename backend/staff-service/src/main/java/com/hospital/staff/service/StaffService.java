package com.hospital.staff.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.staff.repository.StaffRepository;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StaffService extends DomainServiceSupport {
  private final StaffRepository repository;

  public StaffService(
      StaffRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
  public Object list(int page, int size, String q, Jwt jwt) {
    return repository.list("staff", page, size, q, owner(jwt));
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
  public Object get(UUID id, Jwt jwt) {
    var r = repository.get("staff", id);
    if (!owner(jwt).isEmpty()
        && !owner(jwt).equals(Objects.toString(r.get("patientId"), r.get("id").toString())))
      throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
    audit.record(jwt.getSubject(), "READ_staff", id);
    return r;
  }

  @PreAuthorize("hasAnyRole('ADMIN')")
  @Transactional
  public Object create(Map<String, Object> body, Jwt jwt) {
    Validation.required(body, "name");
    Validation.required(body, "department");
    Validation.required(body, "specialty");
    var r = create("staff", body, "staff.created.v1");
    audit.record(jwt.getSubject(), "CREATE_staff", UUID.fromString(r.get("id").toString()));
    return r;
  }

  private String owner(Jwt jwt) {
    return jwt.getClaimAsStringList("roles").contains("PATIENT")
        ? Objects.requireNonNullElse(jwt.getClaimAsString("patientId"), "unbound")
        : "";
  }

  @PreAuthorize("hasRole('ADMIN')")
  public Object update(UUID id, Map<String, Object> b, Jwt jwt) {
    long version = Long.parseLong(Validation.required(b, "version"));
    Validation.required(b, "name");
    Validation.required(b, "department");
    audit.record(jwt.getSubject(), "UPDATE_STAFF", id);
    return update("staff", id, b, version, null);
  }
}
