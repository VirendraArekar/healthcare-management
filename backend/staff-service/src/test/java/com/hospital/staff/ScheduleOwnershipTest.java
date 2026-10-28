package com.hospital.staff;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.AuditService;
import com.hospital.common.util.JsonCodec;
import com.hospital.staff.repository.StaffRepository;
import com.hospital.staff.service.ScheduleService;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

class ScheduleOwnershipTest {
  @AfterEach
  void clear() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doctorCannotCreateAnotherDoctorsWorkingHours() {
    var repository = mock(StaffRepository.class);
    var own = UUID.randomUUID();
    var jwt =
        Jwt.withTokenValue("synthetic")
            .header("alg", "none")
            .subject(UUID.randomUUID().toString())
            .claim("roles", List.of("DOCTOR"))
            .claim("staffId", own.toString())
            .build();
    SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    var service =
        new ScheduleService(
            repository,
            mock(EventPublisher.class),
            mock(AuditService.class),
            new JsonCodec(tools.jackson.databind.json.JsonMapper.builder().build()));
    var error =
        assertThrows(
            ResponseStatusException.class,
            () ->
                service.create(
                    new HashMap<>(
                        Map.of(
                            "staffId",
                            UUID.randomUUID().toString(),
                            "dayOfWeek",
                            "MONDAY",
                            "startsAt",
                            "09:00",
                            "endsAt",
                            "17:00"))));
    assertEquals(403, error.getStatusCode().value());
    verifyNoInteractions(repository);
  }
}
