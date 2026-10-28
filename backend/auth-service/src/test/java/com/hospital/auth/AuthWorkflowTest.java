package com.hospital.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hospital.auth.dto.AuthRequests;
import com.hospital.auth.repository.AuthRepository;
import com.hospital.auth.service.AuthService;
import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.AuditService;
import com.hospital.common.util.JsonCodec;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.web.server.ResponseStatusException;

class AuthWorkflowTest {
  private final AuthRepository repository = mock(AuthRepository.class);
  private final AuthService service =
      new AuthService(
          repository,
          mock(EventPublisher.class),
          mock(AuditService.class),
          new JsonCodec(tools.jackson.databind.json.JsonMapper.builder().build()),
          "unit-only-secret-at-least-thirty-two-characters");

  @Test
  void invalidPasswordIncrementsFailureCount() {
    UUID id = UUID.randomUUID();
    when(repository.findEnabledUserByEmail("user@example.invalid"))
        .thenReturn(
            List.of(
                Map.of(
                    "id",
                    id,
                    "password_hash",
                    new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(4)
                        .encode("correct"))));
    assertThrows(
        ResponseStatusException.class,
        () -> service.login(new AuthRequests.Login("user@example.invalid", "wrong")));
    verify(repository).failedLogin(id);
  }

  @Test
  void reusedRefreshRevokesWholeFamily() {
    UUID family = UUID.randomUUID();
    when(repository.lockRefreshToken(AuthService.hash("replayed")))
        .thenReturn(List.of(Map.of("revoked", true, "family_id", family)));
    assertThrows(
        ResponseStatusException.class, () -> service.refresh(new AuthRequests.Refresh("replayed")));
    verify(repository).revokeFamily(family);
    verify(repository, never()).insertRefreshToken(any(), any(), anyString(), any());
  }

  @Test
  void unknownAccountRejected() {
    assertThrows(
        ResponseStatusException.class,
        () -> service.login(new AuthRequests.Login("unknown@example.invalid", "invalid")));
  }
}
