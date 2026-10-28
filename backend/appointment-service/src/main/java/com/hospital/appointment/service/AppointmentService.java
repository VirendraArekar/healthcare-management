package com.hospital.appointment.service;

import com.hospital.appointment.repository.AppointmentRepository;
import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppointmentService extends DomainServiceSupport {
  private final AppointmentRepository repository;

  public AppointmentService(
      AppointmentRepository repository,
      EventPublisher events,
      AuditService audit,
      JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
  public Object slot(Map<String, Object> b) {
    referenceCache.invalidate("availability:v1");
    UUID doctor = UUID.fromString(Validation.required(b, "doctorId"));
    Instant start = Instant.parse(Validation.required(b, "startsAt"));
    Instant end = Instant.parse(Validation.required(b, "endsAt"));
    Validation.require(start.isAfter(Instant.now()) && end.isAfter(start), "Invalid slot");
    var actor = com.hospital.common.security.Actor.current();
    if (com.hospital.common.security.Actor.has(actor, "DOCTOR"))
      Validation.require(
          doctor.toString().equals(actor.getClaimAsString("staffId")),
          "Doctors may publish only their own slots");
    directory.staff(doctor);
    directory.available(doctor, start, end);
    UUID id = UUID.randomUUID();
    repository.insertSlot(id, doctor, java.sql.Timestamp.from(start), java.sql.Timestamp.from(end));
    return Map.of("id", id);
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','PATIENT')")
  public Object slots() {
    return referenceCache
        .get(
            "availability:v1",
            java.time.Duration.ofSeconds(30),
            () -> Map.of("content", repository.findAvailableSlots()))
        .get("content");
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PATIENT')")
  public Object list(int page, int size, Jwt jwt) {
    Validation.require(page >= 0 && size > 0 && size <= 100, "Pagination");
    boolean patient = jwt.getClaimAsStringList("roles").contains("PATIENT");
    String owner =
        patient ? Objects.requireNonNullElse(jwt.getClaimAsString("patientId"), "unbound") : "";
    String doctor =
        com.hospital.common.security.Actor.has(jwt, "DOCTOR")
            ? Objects.requireNonNullElse(jwt.getClaimAsString("staffId"), "unbound")
            : "";
    var rows = repository.findBookings(owner, owner, doctor, doctor, size, (long) page * size);
    return Map.of("content", rows);
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  @Transactional
  public Object book(Map<String, Object> b, Jwt jwt) {
    referenceCache.invalidate("availability:v1");
    UUID slot = UUID.fromString(Validation.required(b, "slotId"));
    UUID patient = UUID.fromString(Validation.required(b, "patientId"));
    directory.patient(patient);
    var slots = repository.lockFutureSlot(slot);
    Validation.require(!slots.isEmpty(), "Slot unavailable");
    directory.available(
        (UUID) slots.getFirst().get("doctor_id"),
        ((java.sql.Timestamp) slots.getFirst().get("starts_at")).toInstant(),
        ((java.sql.Timestamp) slots.getFirst().get("ends_at")).toInstant());
    UUID id = UUID.randomUUID();
    repository.insertBooking(id, slot, patient);
    events.publish(
        "appointment.booked.v1",
        id,
        Map.of(
            "appointmentId",
            id.toString(),
            "patientId",
            patient.toString(),
            "doctorId",
            slots.getFirst().get("doctor_id").toString(),
            "startsAt",
            slots.getFirst().get("starts_at").toString()));
    audit.record(jwt.getSubject(), "BOOK", id);
    return Map.of("id", id, "status", "BOOKED");
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','PATIENT')")
  @Transactional
  public Object cancel(UUID id, Jwt jwt) {
    referenceCache.invalidate("availability:v1");
    var booking = repository.lockBooking(id);
    Validation.require(!booking.isEmpty(), "Booking not found");
    if (com.hospital.common.security.Actor.has(jwt, "PATIENT"))
      Validation.require(
          ((java.sql.Timestamp) booking.getFirst().get("starts_at"))
              .toInstant()
              .isAfter(Instant.now().plusSeconds(7200)),
          "Patient cancellations require at least two hours notice");
    String owner =
        jwt.getClaimAsStringList("roles").contains("PATIENT")
            ? Objects.requireNonNullElse(jwt.getClaimAsString("patientId"), "unbound")
            : "";
    int n = repository.cancelBooking(id, owner, owner);
    Validation.require(n == 1, "Booking missing, cancelled, or inaccessible");
    events.publish(
        "appointment.cancelled.v1",
        id,
        Map.of(
            "appointmentId",
            id.toString(),
            "patientId",
            booking.getFirst().get("patient_id").toString()));
    return Map.of("status", "CANCELLED");
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  @Transactional
  public Object reschedule(UUID id, Map<String, Object> b) {
    referenceCache.invalidate("availability:v1");
    UUID slot = UUID.fromString(Validation.required(b, "slotId"));
    var target = repository.lockRescheduleSlot(slot);
    Validation.require(!target.isEmpty(), "New slot is unavailable");
    var booking = repository.lockBooking(id);
    Validation.require(
        !booking.isEmpty() && "BOOKED".equals(booking.getFirst().get("status")),
        "Booking is not active");
    var window = target.getFirst();
    directory.available(
        (UUID) window.get("doctor_id"),
        ((java.sql.Timestamp) window.get("starts_at")).toInstant(),
        ((java.sql.Timestamp) window.get("ends_at")).toInstant());
    Validation.require(repository.rescheduleBooking(slot, id) == 1, "Booking unavailable");
    events.publish(
        "appointment.rescheduled.v1",
        id,
        Map.of(
            "appointmentId",
            id.toString(),
            "patientId",
            booking.getFirst().get("patient_id").toString(),
            "doctorId",
            window.get("doctor_id").toString()));
    return Map.of("id", id, "slotId", slot);
  }

  @PreAuthorize("hasRole('DOCTOR')")
  public Object complete(UUID id, Jwt jwt) {
    var rows = repository.lockBooking(id);
    Validation.require(!rows.isEmpty(), "Appointment missing");
    Validation.require(
        rows.getFirst().get("doctor_id").toString().equals(jwt.getClaimAsString("staffId")),
        "Appointment belongs to another doctor");
    Validation.require(
        !((java.sql.Timestamp) rows.getFirst().get("starts_at")).toInstant().isAfter(Instant.now()),
        "Appointment has not started");
    UUID patient = (UUID) rows.getFirst().get("patient_id");
    directory.care(patient);
    Validation.require(
        repository.completeBooking(id, "COMPLETED") == 1, "Appointment is not booked");
    audit.record(jwt.getSubject(), "COMPLETE_APPOINTMENT", id);
    return Map.of("id", id, "status", "COMPLETED");
  }

  @PreAuthorize("hasRole('PATIENT')")
  public Object request(Map<String, Object> b, Jwt jwt) {
    UUID patient = com.hospital.common.security.Actor.patient(jwt);
    directory.patient(patient);
    UUID slot = UUID.fromString(Validation.required(b, "slotId"));
    Validation.require(!repository.lockFutureSlot(slot).isEmpty(), "Invalid future slot");
    return create(
        "appointment-request",
        Map.of("patientId", patient.toString(), "slotId", slot.toString(), "status", "REQUESTED"),
        null);
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','PATIENT')")
  public Object requests(int page, Jwt jwt) {
    return repository.list(
        "appointment-request",
        page,
        20,
        "",
        com.hospital.common.security.Actor.has(jwt, "PATIENT")
            ? com.hospital.common.security.Actor.patient(jwt).toString()
            : "");
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object approve(UUID id, Jwt jwt) {
    Validation.require(!repository.lockRequest(id).isEmpty(), "Request missing");
    var request = repository.get("appointment-request", id);
    Validation.require("REQUESTED".equals(request.get("status")), "Request already processed");
    var booking =
        book(Map.of("patientId", request.get("patientId"), "slotId", request.get("slotId")), jwt);
    long version = ((Number) request.remove("version")).longValue();
    request.put("status", "APPROVED");
    update("appointment-request", id, request, version, null);
    return booking;
  }

  public Object detail(UUID id, Jwt jwt) {
    var row = repository.detail(id);
    if (com.hospital.common.security.Actor.has(jwt, "PATIENT"))
      Validation.require(
          com.hospital.common.security.Actor.patient(jwt).toString().equals(row.get("patientId")),
          "Appointment inaccessible");
    if (com.hospital.common.security.Actor.has(jwt, "DOCTOR"))
      Validation.require(
          Objects.equals(jwt.getClaimAsString("staffId"), row.get("doctorId")),
          "Appointment belongs to another doctor");
    return row;
  }

  @PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF')")
  public Object utilization(java.time.LocalDate from, java.time.LocalDate until) {
    Validation.require(
        until.isAfter(from) && java.time.temporal.ChronoUnit.DAYS.between(from, until) <= 366,
        "Choose a report window of 1 to 366 days");
    return Map.of(
        "content",
        repository.utilization(
            java.sql.Timestamp.from(from.atStartOfDay(java.time.ZoneOffset.UTC).toInstant()),
            java.sql.Timestamp.from(until.atStartOfDay(java.time.ZoneOffset.UTC).toInstant())),
        "from",
        from,
        "until",
        until,
        "basis",
        "occupied minutes / published available minutes; UTC; end date exclusive");
  }
}
