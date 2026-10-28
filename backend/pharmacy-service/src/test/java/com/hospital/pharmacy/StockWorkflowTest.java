package com.hospital.pharmacy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hospital.common.client.DirectoryClient;
import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.AuditService;
import com.hospital.common.util.JsonCodec;
import com.hospital.pharmacy.repository.PharmacyRepository;
import com.hospital.pharmacy.service.PharmacyService;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

class StockWorkflowTest {
  private final PharmacyRepository repository = mock(PharmacyRepository.class);
  private final EventPublisher events = mock(EventPublisher.class);
  private final DirectoryClient directory = mock(DirectoryClient.class);
  private final PharmacyService service =
      new PharmacyService(
          repository,
          events,
          mock(AuditService.class),
          new JsonCodec(tools.jackson.databind.json.JsonMapper.builder().build()));

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(service, "directory", directory);
    when(repository.findBatch(any())).thenReturn(List.of(Map.of("medicine", "Synthetic")));
  }

  @Test
  void failedAtomicStockUpdateCannotPublishDispense() {
    UUID prescription = UUID.randomUUID();
    when(directory.prescription(prescription))
        .thenReturn(
            Map.of(
                "status",
                "ACTIVE",
                "validUntil",
                "2099-01-01",
                "quantity",
                100,
                "medicine",
                "Synthetic"));
    when(repository.findBatch(any())).thenReturn(List.of(Map.of("medicine", "Synthetic")));
    when(repository.reservePrescription(prescription, 10, 100)).thenReturn(1);
    var jwt = Jwt.withTokenValue("unit").header("alg", "none").subject("pharmacist").build();
    assertThrows(
        ResponseStatusException.class,
        () ->
            service.dispense(
                UUID.randomUUID(),
                Map.of("quantity", 10, "prescriptionId", prescription.toString()),
                jwt));
    verifyNoInteractions(events);
  }

  @Test
  void negativeDispenseRejectedBeforeDatabaseWrite() {
    assertThrows(
        ResponseStatusException.class,
        () ->
            service.dispense(
                UUID.randomUUID(),
                Map.of("quantity", -1, "prescriptionId", UUID.randomUUID().toString()),
                null));
    verifyNoInteractions(repository, directory);
  }

  @Test
  void expiredReceiptRejected() {
    assertThrows(
        ResponseStatusException.class,
        () ->
            service.receive(
                Map.of("medicine", "Synthetic", "quantity", 1, "expiresOn", "2000-01-01")));
    verifyNoInteractions(repository);
  }

  @Test
  void prescriptionCapPreventsStockMutation() {
    UUID id = UUID.randomUUID();
    when(directory.prescription(id))
        .thenReturn(
            Map.of(
                "status",
                "ACTIVE",
                "validUntil",
                "2099-01-01",
                "quantity",
                1,
                "medicine",
                "Synthetic"));
    assertThrows(
        ResponseStatusException.class,
        () ->
            service.dispense(
                UUID.randomUUID(), Map.of("quantity", 2, "prescriptionId", id.toString()), null));
    verify(repository).reservePrescription(id, 2, 1);
    verify(repository, never()).decreaseStock(anyInt(), any(), anyInt());
  }
}
