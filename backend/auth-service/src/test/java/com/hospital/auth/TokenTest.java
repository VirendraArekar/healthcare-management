package com.hospital.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TokenTest {
  @Test
  void hashIsDeterministicAndNotRawToken() {
    String raw = "synthetic-refresh-token";
    assertEquals(
        com.hospital.auth.service.AuthService.hash(raw),
        com.hospital.auth.service.AuthService.hash(raw));
    assertEquals(64, com.hospital.auth.service.AuthService.hash(raw).length());
    assertNotEquals(raw, com.hospital.auth.service.AuthService.hash(raw));
    assertNotEquals(
        com.hospital.auth.service.AuthService.hash(raw),
        com.hospital.auth.service.AuthService.hash(raw + "different"));
  }

  @Test
  void missingRefreshTokenRejected() {
    assertThrows(
        IllegalArgumentException.class, () -> com.hospital.auth.service.AuthService.hash(null));
  }
}
