package com.hospital.staff.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.staff.repository.StaffRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScheduleService extends DomainServiceSupport {
  private final StaffRepository repository;

  public ScheduleService(
      StaffRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  public Object list(int page) {
    Validation.page(page, 20);
    return repository.list("schedule", page, 20, "", "");
  }

  public Object create(Map<String, Object> b) {
    Validation.required(b, "staffId");
    Validation.required(b, "dayOfWeek");
    Validation.required(b, "startsAt");
    Validation.required(b, "endsAt");
    var actor = com.hospital.common.security.Actor.current();
    if (!com.hospital.common.security.Actor.has(actor, "ADMIN")
        && !Validation.required(b, "staffId").equals(actor.getClaimAsString("staffId")))
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN,
          "Doctors may manage only their own working hours");
    repository.get("staff", UUID.fromString(Validation.required(b, "staffId")));
    java.time.DayOfWeek.valueOf(b.get("dayOfWeek").toString());
    Validation.require(
        java.time.LocalTime.parse(b.get("endsAt").toString())
            .isAfter(java.time.LocalTime.parse(b.get("startsAt").toString())),
        "Invalid working hours");
    return create("schedule", b, null);
  }
}
