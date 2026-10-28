package com.hospital.common.event;

import com.hospital.common.util.Validation;
import java.time.Instant;
import java.util.*;

public record DomainEvent(
    UUID eventId,
    String eventType,
    int eventVersion,
    Instant occurredAt,
    String correlationId,
    UUID aggregateId,
    Map<String, Object> payload) {
  public DomainEvent {
    Objects.requireNonNull(eventId);
    Objects.requireNonNull(aggregateId);
    Objects.requireNonNull(occurredAt);
    Validation.require(
        eventType != null && eventType.matches("[a-z-]+[.][a-z-]+[.]v1"), "Invalid event type");
    Validation.require(eventVersion == 1, "Unsupported event version");
    Validation.require(
        correlationId != null && correlationId.length() <= 64, "Invalid correlation ID");
    payload = Map.copyOf(Objects.requireNonNull(payload));
    Validation.require(
        java.util.Collections.disjoint(
            payload.keySet(),
            java.util.Set.of(
                "password",
                "passwordHash",
                "refreshToken",
                "token",
                "notes",
                "diagnosis",
                "treatment",
                "result",
                "allergies")),
        "Sensitive fields are forbidden in workflow events");
  }
}
