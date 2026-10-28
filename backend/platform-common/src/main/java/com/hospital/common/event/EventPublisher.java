package com.hospital.common.event;

import com.hospital.common.repository.EventRepository;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EventPublisher {
  private final EventRepository repository;

  @org.springframework.beans.factory.annotation.Autowired(required = false)
  private List<DomainEventPolicy> policies = List.of();

  public EventPublisher(EventRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public void publish(String type, UUID id, Map<String, Object> payload) {
    if (type == null) return;
    var event =
        new DomainEvent(
            UUID.randomUUID(),
            type,
            1,
            Instant.now(),
            Optional.ofNullable(org.slf4j.MDC.get("correlationId"))
                .orElse(UUID.randomUUID().toString()),
            id,
            payload);
    policies.forEach(policy -> policy.validate(event));
    repository.save(event);
  }
}
