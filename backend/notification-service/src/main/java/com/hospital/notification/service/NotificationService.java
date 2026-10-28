package com.hospital.notification.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.notification.repository.NotificationRepository;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationService extends DomainServiceSupport {
  private final NotificationRepository repository;

  public NotificationService(
      NotificationRepository repository,
      EventPublisher events,
      AuditService audit,
      JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @PreAuthorize(
      "hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','LAB_TECHNICIAN','PHARMACIST','BILLING_STAFF','PATIENT')")
  public Object list(int page, org.springframework.security.oauth2.jwt.Jwt jwt) {
    var audience = com.hospital.notification.model.NotificationAudience.forActor(jwt);
    return repository.audience(
        page,
        20,
        audience.prefixes(),
        audience.patientId(),
        audience.assignedOnly() ? directory.assignedPatients() : null,
        UUID.fromString(jwt.getSubject()));
  }

  public Object read(UUID id, org.springframework.security.oauth2.jwt.Jwt jwt) {
    var row = repository.get("notification", id);
    var audience = com.hospital.notification.model.NotificationAudience.forActor(jwt);
    if (audience.patientId() != null)
      Validation.require(
          audience.patientId().toString().equals(row.get("patientId")),
          "Notification inaccessible");
    if (!audience.prefixes().isEmpty())
      Validation.require(
          audience.prefixes().stream()
              .anyMatch(prefix -> row.get("eventType").toString().startsWith(prefix)),
          "Notification is outside your workflow");
    if (audience.assignedOnly()) directory.care(UUID.fromString(row.get("patientId").toString()));
    repository.markRead(id, UUID.fromString(jwt.getSubject()));
    return Map.of("id", id, "read", true);
  }

  public Object failed() {
    return repository.failedEmails();
  }

  public Object retry(UUID id) {
    repository.retryEmail(id);
    return Map.of("status", "QUEUED");
  }
}
