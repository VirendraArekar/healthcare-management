package com.hospital.auth.controller;

import com.hospital.auth.dto.RecoveryRequests.*;
import com.hospital.auth.service.AccountRecoveryService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AccountRecoveryController {
  private final AccountRecoveryService service;

  public AccountRecoveryController(AccountRecoveryService service) {
    this.service = service;
  }

  @PostMapping("/forgot-password")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Object forgot(@Valid @RequestBody Forgot request) {
    service.forgot(request.email());
    return Map.of("message", "If the account exists, recovery instructions have been sent.");
  }

  @PostMapping("/reset-password")
  public Object reset(@Valid @RequestBody Reset request) {
    service.reset(request.token(), request.password());
    return Map.of("status", "PASSWORD_RESET");
  }
}
