package com.hospital.common.event;

public interface DomainEventPolicy {
  void validate(DomainEvent event);
}
