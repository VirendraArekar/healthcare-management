package com.hospital.notification.event;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.notification.repository.NotificationRepository;
import java.util.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventConsumer extends DomainServiceSupport {
  private final NotificationRepository repository;

  public EventConsumer(
      NotificationRepository repository,
      EventPublisher events,
      AuditService audit,
      JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @KafkaListener(
      topicPattern =
          "(patient\\..*|appointment\\..*|lab\\..*|pharmacy\\..*|billing\\..*|notification\\..*)\\.v1")
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
    repository.enqueueEmail(eventId, "Hospital workflow update", e.get("eventType").toString());
    var payload = (Map<String, Object>) e.get("payload");
    var notification = new LinkedHashMap<String, Object>();
    notification.put("eventType", e.get("eventType"));
    notification.put("aggregateId", e.get("aggregateId"));
    notification.put(
        "message", payload.getOrDefault("message", "A hospital workflow status changed"));
    notification.put("read", false);
    if (payload.get("patientId") != null) notification.put("patientId", payload.get("patientId"));
    if (Set.of("patient.registered.v1", "patient.updated.v1").contains(e.get("eventType"))
        && payload.get("email") != null)
      repository.contact(
          UUID.fromString(e.get("aggregateId").toString()), payload.get("email").toString());
    Object patient = payload.get("patientId");
    if (patient != null) repository.routeEmail(eventId, UUID.fromString(patient.toString()));
    create("notification", notification, null);
  }
}
