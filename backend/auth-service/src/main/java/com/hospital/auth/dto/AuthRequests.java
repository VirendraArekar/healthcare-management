package com.hospital.auth.dto;

import jakarta.validation.constraints.*;

public final class AuthRequests {
  private AuthRequests() {}

  public record Login(@NotBlank @Email String email, @NotBlank String password) {}

  public record Refresh(@NotBlank String refreshToken) {}
}
