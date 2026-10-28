package com.hospital.notification;

import static org.junit.jupiter.api.Assertions.*;

import com.hospital.common.event.DomainEvent;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;

class EventContractTest {
  @Test
  void rejectsUnsupportedVersion() {
    assertThrows(
        RuntimeException.class,
        () ->
            new DomainEvent(
                UUID.randomUUID(),
                "patient.registered.v1",
                2,
                Instant.now(),
                "correlation",
                UUID.randomUUID(),
                Map.of()));
  }

  @Test
  void requiresEventIdentity() {
    assertThrows(
        NullPointerException.class,
        () ->
            new DomainEvent(
                null,
                "patient.registered.v1",
                1,
                Instant.now(),
                "correlation",
                UUID.randomUUID(),
                Map.of()));
  }

  @Test
  void rejectsUnversionedType() {
    assertThrows(
        RuntimeException.class,
        () ->
            new DomainEvent(
                UUID.randomUUID(),
                "unversioned",
                1,
                Instant.now(),
                "correlation",
                UUID.randomUUID(),
                Map.of()));
  }

  @Test
  void payloadIsImmutable() {
    var payload = new HashMap<String, Object>();
    payload.put("patientId", UUID.randomUUID().toString());
    var event =
        new DomainEvent(
            UUID.randomUUID(),
            "patient.registered.v1",
            1,
            Instant.now(),
            "correlation",
            UUID.randomUUID(),
            payload);
    payload.clear();
    assertEquals(1, event.payload().size());
    assertThrows(UnsupportedOperationException.class, () -> event.payload().clear());
  }
}
