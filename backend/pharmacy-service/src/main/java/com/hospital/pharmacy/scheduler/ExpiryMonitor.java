package com.hospital.pharmacy.scheduler;

import com.hospital.common.event.EventPublisher;
import com.hospital.pharmacy.repository.PharmacyRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExpiryMonitor {
  private final PharmacyRepository repository;
  private final EventPublisher events;
  private final int days;

  public ExpiryMonitor(
      PharmacyRepository repository,
      EventPublisher events,
      @Value("${EXPIRY_ALERT_DAYS:30}") int days) {
    this.repository = repository;
    this.events = events;
    this.days = days;
  }

  @Scheduled(fixedDelayString = "${EXPIRY_SCAN_MILLIS:3600000}")
  @Transactional
  public void scan() {
    for (var batch : repository.expiring(days)) {
      var id = (UUID) batch.get("id");
      if (repository.claimAlert(id, "EXPIRY"))
        events.publish(
            "pharmacy.stock-expiring.v1",
            id,
            Map.of(
                "batchId",
                id.toString(),
                "expiresOn",
                batch.get("expires_on").toString(),
                "medicine",
                batch.get("medicine")));
    }
  }
}
