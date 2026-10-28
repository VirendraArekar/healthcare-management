package com.hospital.appointment.scheduler;

import com.hospital.appointment.repository.AppointmentRepository;
import com.hospital.common.event.EventPublisher;
import java.util.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReminderScheduler {
  private final AppointmentRepository repository;
  private final EventPublisher events;

  public ReminderScheduler(AppointmentRepository repository, EventPublisher events) {
    this.repository = repository;
    this.events = events;
  }

  @Scheduled(fixedDelay = 60000)
  @Transactional
  public void schedule() {
    for (var row : repository.upcomingReminders()) {
      events.publish(
          "notification.requested.v1",
          (UUID) row.get("id"),
          Map.of(
              "patientId",
              row.get("patient_id").toString(),
              "message",
              "Upcoming appointment reminder"));
      repository.markReminded(row.get("id"));
    }
  }
}
