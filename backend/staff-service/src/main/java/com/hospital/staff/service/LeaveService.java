package com.hospital.staff.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.security.Actor;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.staff.repository.StaffRepository;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LeaveService extends DomainServiceSupport {
  private final StaffRepository repository;

  public LeaveService(
      StaffRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  public Object list(int page) {
    Validation.page(page, 20);
    return repository.list("leave", page, 20, "", "");
  }

  public Object create(Map<String, Object> b) {
    var actor = Actor.current();
    UUID staff = UUID.fromString(Validation.required(b, "staffId"));
    repository.get("staff", staff);
    if (!Actor.has(actor, "ADMIN"))
      Validation.require(
          staff.toString().equals(actor.getClaimAsString("staffId")),
          "Staff may request only their own leave");
    LocalDate start = LocalDate.parse(Validation.required(b, "startsOn")),
        end = LocalDate.parse(Validation.required(b, "endsOn"));
    Validation.require(!end.isBefore(start), "Leave ends before it starts");
    b.put("status", "PENDING");
    b.put("requestedBy", actor.getSubject());
    return create("leave", b, null);
  }

  public Object decide(UUID id, Map<String, Object> b) {
    var actor = Actor.current();
    var leave = repository.get("leave", id);
    Validation.require("PENDING".equals(leave.get("status")), "Leave request is already processed");
    String status = Validation.required(b, "status");
    Validation.require(Set.of("APPROVED", "REJECTED").contains(status), "Invalid leave decision");
    long version = ((Number) leave.remove("version")).longValue();
    leave.put("status", status);
    leave.put("approvedBy", actor.getSubject());
    var result = update("leave", id, leave, version, null);
    if (status.equals("APPROVED"))
      events.publish(
          "staff.leave-approved.v1",
          id,
          Map.of(
              "staffId",
              leave.get("staffId"),
              "startsOn",
              leave.get("startsOn"),
              "endsOn",
              leave.get("endsOn")));
    audit.record(actor.getSubject(), "LEAVE_" + status, id);
    return result;
  }
}
