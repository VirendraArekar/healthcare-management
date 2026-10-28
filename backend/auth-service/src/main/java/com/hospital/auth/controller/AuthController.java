package com.hospital.auth.controller;

import com.hospital.auth.dto.AuthRequests.*;
import com.hospital.auth.service.AuthService;
import java.security.*;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/login")
  public Object login(@jakarta.validation.Valid @RequestBody Login login) {
    return service.login(login);
  }

  @PostMapping("/refresh")
  public Object refresh(@jakarta.validation.Valid @RequestBody Refresh request) {
    return service.refresh(request);
  }

  @PostMapping("/logout")
  public Object logout(
      @jakarta.validation.Valid @RequestBody Refresh request, @AuthenticationPrincipal Jwt jwt) {
    return service.logout(request, jwt);
  }

  @GetMapping("/me")
  public Object me(@AuthenticationPrincipal Jwt jwt) {
    return service.me(jwt);
  }

  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public Object users() {
    return service.users();
  }

  @PostMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public Object user(@jakarta.validation.Valid @RequestBody Map<String, Object> b) {
    return service.user(b);
  }

  @PatchMapping("/users/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Object account(
      @PathVariable UUID id, @RequestBody Map<String, Object> b, @AuthenticationPrincipal Jwt jwt) {
    return service.account(id, b, jwt);
  }

  @GetMapping("/users/{id}/reference")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object reference(@PathVariable UUID id) {
    return service.reference(id);
  }

  @GetMapping("/clinicians")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object clinicians() {
    return service.clinicians();
  }
}
