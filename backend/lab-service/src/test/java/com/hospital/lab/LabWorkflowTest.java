package com.hospital.lab;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.AuditService;
import com.hospital.common.util.JsonCodec;
import com.hospital.lab.repository.LaboratoryRepository;
import com.hospital.lab.service.LaboratoryService;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

class LabWorkflowTest {
  private final LaboratoryRepository repository = mock(LaboratoryRepository.class);
  private final AuditService audit = mock(AuditService.class);
  private final EventPublisher events = mock(EventPublisher.class);
  private final LaboratoryService service =
      new LaboratoryService(
          repository,
          events,
          audit,
          new JsonCodec(tools.jackson.databind.json.JsonMapper.builder().build()));
  private final UUID id = UUID.randomUUID();

  private Jwt user(String name) {
    return Jwt.withTokenValue("unit").header("alg", "none").subject(name).build();
  }

  @Test
  void cannotSkipSampleCollection() {
    when(repository.get("order", id))
        .thenReturn(new HashMap<>(Map.of("status", "ORDERED", "version", 0)));
    assertThrows(
        ResponseStatusException.class,
        () ->
            service.transition(
                id, Map.of("status", "VERIFIED"), user("10000000-0000-0000-0000-000000000001")));
    verify(repository, never()).update(anyString(), any(), anyMap(), anyLong());
  }

  @Test
  void verifierMustDifferFromResultAuthor() {
    when(repository.get("order", id))
        .thenReturn(
            new HashMap<>(
                Map.of(
                    "status",
                    "RESULT_ENTERED",
                    "enteredBy",
                    "10000000-0000-0000-0000-000000000001",
                    "version",
                    0)));
    assertThrows(
        ResponseStatusException.class,
        () ->
            service.transition(
                id, Map.of("status", "VERIFIED"), user("10000000-0000-0000-0000-000000000001")));
    verifyNoInteractions(events);
  }

  @Test
  void secondTechnicianCanVerify() {
    when(repository.get("order", id))
        .thenReturn(
            new HashMap<>(
                Map.of(
                    "status",
                    "RESULT_ENTERED",
                    "enteredBy",
                    "10000000-0000-0000-0000-000000000001",
                    "version",
                    2)));
    when(repository.update(eq("order"), eq(id), anyMap(), eq(2L)))
        .thenReturn(Map.of("id", id, "status", "VERIFIED"));
    assertEquals(
        "VERIFIED",
        ((Map<?, ?>)
                service.transition(
                    id, Map.of("status", "VERIFIED"), user("10000000-0000-0000-0000-000000000002")))
            .get("status"));
    verify(audit).record("10000000-0000-0000-0000-000000000002", "LAB_VERIFIED", id);
    verify(events).publish(eq("lab.result-verified.v1"), eq(id), anyMap());
  }
}
