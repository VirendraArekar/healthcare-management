package com.hospital.common.event;

import java.util.concurrent.TimeUnit;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
  private final com.hospital.common.repository.OutboxRepository repository;
  private final KafkaTemplate<String, String> kafka;

  public OutboxPublisher(
      com.hospital.common.repository.OutboxRepository repository,
      KafkaTemplate<String, String> kafka) {
    this.repository = repository;
    this.kafka = kafka;
  }

  @Scheduled(fixedDelayString = "${OUTBOX_DELAY:3000}")
  @Transactional
  public void publish() {
    for (var row : repository.pending()) {
      try {
        kafka
            .send(
                row.get("topic").toString(),
                row.get("aggregate_id").toString(),
                row.get("payload").toString())
            .get(10, TimeUnit.SECONDS);
        repository.sent(row.get("id"));
      } catch (Exception e) {
        if (e instanceof InterruptedException) Thread.currentThread().interrupt();
        throw new IllegalStateException("Outbox publish deferred", e);
      }
    }
  }
}
