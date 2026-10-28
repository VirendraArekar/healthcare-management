package com.hospital.lab.event;

import com.hospital.common.event.*;
import com.hospital.common.util.Validation;
import org.springframework.stereotype.Component;

@Component
public class LaboratoryEventPolicy implements DomainEventPolicy {
  public void validate(DomainEvent event) {
    Validation.require(
        event.eventType().startsWith("lab."), "Topic does not belong to this publishing service");
    Validation.require(!event.payload().isEmpty(), "Domain event payload is required");
  }
}
