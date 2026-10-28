package com.hospital.staff.controller;

import com.hospital.staff.dto.StaffRequest;
import com.hospital.staff.service.StaffService;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
  private final StaffService service;

  public StaffController(StaffService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
  public Object list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "") String q,
      @AuthenticationPrincipal Jwt jwt) {
    return service.list(page, size, q, jwt);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
  public Object get(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.get(id, jwt);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('ADMIN')")
  public Object create(
      @jakarta.validation.Valid @RequestBody StaffRequest body, @AuthenticationPrincipal Jwt jwt) {
    return service.create(body.toMap(), jwt);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Object update(
      @PathVariable UUID id, @RequestBody Map<String, Object> b, @AuthenticationPrincipal Jwt jwt) {
    return service.update(id, b, jwt);
  }
}
