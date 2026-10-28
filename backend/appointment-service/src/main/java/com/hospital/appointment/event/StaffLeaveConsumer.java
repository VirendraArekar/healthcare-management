package com.hospital.appointment.event;

import com.hospital.appointment.repository.AppointmentRepository;
import com.hospital.common.event.EventPublisher;
import java.time.LocalDate;
import java.util.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
public class StaffLeaveConsumer {
  private final AppointmentRepository repository;
  private final EventPublisher events;
  private final ObjectMapper mapper;

  @org.springframework.beans.factory.annotation.Autowired
  private com.hospital.common.service.ReferenceCache cache;

  public StaffLeaveConsumer(
      AppointmentRepository repository, EventPublisher events, ObjectMapper mapper) {
    this.repository = repository;
    this.events = events;
    this.mapper = mapper;
  }

  @KafkaListener(topics = "staff.leave-approved.v1")
  @Transactional
  public void consume(String value) throws Exception {
    var event = mapper.readValue(value, com.hospital.common.event.DomainEvent.class);
    if (!repository.claimLeaveEvent(event.eventId())) return;
    var p = event.payload();
    var cancelled =
        repository.withdrawForLeave(
            UUID.fromString(p.get("staffId").toString()),
            LocalDate.parse(p.get("startsOn").toString()),
            LocalDate.parse(p.get("endsOn").toString()));
    cache.invalidate("availability:v1");
    for (var row : cancelled)
      events.publish(
          "appointment.cancelled.v1",
          (UUID) row.get("id"),
          Map.of(
              "appointmentId",
              row.get("id").toString(),
              "patientId",
              row.get("patient_id").toString(),
              "reason",
              "DOCTOR_LEAVE"));
  }
}
