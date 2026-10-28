package com.hospital.patient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.AuditService;
import com.hospital.common.util.JsonCodec;
import com.hospital.patient.repository.PatientRepository;
import com.hospital.patient.service.PatientService;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

class CareAccessTest {
  @Test
  void unassignedClinicianCannotRead() {
    var repository = mock(PatientRepository.class);
    var service =
        new PatientService(
            repository,
            mock(EventPublisher.class),
            mock(AuditService.class),
            new JsonCodec(tools.jackson.databind.json.JsonMapper.builder().build()));
    UUID patient = UUID.randomUUID();
    when(repository.get("patient", patient)).thenReturn(Map.of("id", patient));
    var jwt =
        Jwt.withTokenValue("unit")
            .header("alg", "none")
            .subject(UUID.randomUUID().toString())
            .claim("roles", List.of("DOCTOR"))
            .build();
    var error = assertThrows(ResponseStatusException.class, () -> service.get(patient, jwt));
    assertEquals(403, error.getStatusCode().value());
  }
}
