package com.hospital.reporting.event;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.reporting.repository.ReportingRepository;
import java.util.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventConsumer extends DomainServiceSupport {
  private final ReportingRepository repository;

  public EventConsumer(
      ReportingRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @KafkaListener(
      topicPattern = "(patient\\..*|appointment\\..*|lab\\..*|pharmacy\\..*|billing\\..*)\\.v1")
  @Transactional
  public void consume(String value) {
    var e = codec.object(value);
    var envelope =
        tools.jackson.databind.json.JsonMapper.builder()
            .build()
            .convertValue(e, com.hospital.common.event.DomainEvent.class);
    Validation.require(
        ((Number) e.get("eventVersion")).intValue() == 1, "Unsupported event version");
    UUID eventId = UUID.fromString(Validation.required(e, "eventId"));
    if (repository.claimEvent(eventId) == 0) return;
    repository.projectEvent(e.get("eventType").toString());
    var payload = (Map<String, Object>) e.get("payload");
    String type = e.get("eventType").toString();
    java.math.BigDecimal amount =
        (type.equals("billing.payment-completed.v1") || type.equals("billing.refund-completed.v1"))
            ? new java.math.BigDecimal(payload.getOrDefault("amount", 0).toString())
            : java.math.BigDecimal.ZERO;
    repository.daily(
        java.time.Instant.parse(e.get("occurredAt").toString())
            .atZone(java.time.ZoneOffset.UTC)
            .toLocalDate(),
        type,
        amount);
    if (type.equals("appointment.booked.v1") && payload.get("doctorId") != null)
      repository.doctor(UUID.fromString(payload.get("doctorId").toString()));
  }
}
