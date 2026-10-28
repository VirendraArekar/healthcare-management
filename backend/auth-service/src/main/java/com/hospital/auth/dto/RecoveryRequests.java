package com.hospital.auth.dto;

import jakarta.validation.constraints.*;

public final class RecoveryRequests {
  private RecoveryRequests() {}

  public record Forgot(@NotBlank @Email String email) {}

  public record Reset(
      @NotBlank String token, @NotBlank @Size(min = 12, max = 200) String password) {}
}
