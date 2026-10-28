package com.hospital.appointment.event;

import com.hospital.common.event.*;
import com.hospital.common.util.Validation;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventPolicy implements DomainEventPolicy {
  public void validate(DomainEvent event) {
    Validation.require(
        event.eventType().startsWith("appointment.")
            || event.eventType().startsWith("notification."),
        "Topic does not belong to this publishing service");
    Validation.require(!event.payload().isEmpty(), "Domain event payload is required");
  }
}
