package com.hospital.staff.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.staff.repository.StaffRepository;
import java.time.Duration;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DepartmentService extends DomainServiceSupport {
  private final StaffRepository repository;

  public DepartmentService(
      StaffRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
  public Object list() {
    return referenceCache.get(
        "departments:v1",
        Duration.ofMinutes(5),
        () -> repository.list("department", 0, 100, "", ""));
  }

  @PreAuthorize("hasRole('ADMIN')")
  public Object create(Map<String, Object> b) {
    Validation.required(b, "name");
    var result = create("department", b, null);
    referenceCache.invalidate("departments:v1");
    return result;
  }

  public Object update(java.util.UUID id, Map<String, Object> b) {
    var current = repository.get("department", id);
    long version = Long.parseLong(Validation.required(b, "version"));
    current.putAll(b);
    Validation.required(current, "name");
    var response = update("department", id, current, version, null);
    referenceCache.invalidate("departments:v1");
    return response;
  }
}
