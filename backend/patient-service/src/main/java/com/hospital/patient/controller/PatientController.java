package com.hospital.patient.controller;

import com.hospital.patient.dto.PatientRequest;
import com.hospital.patient.service.PatientService;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
  private final PatientService service;

  public PatientController(PatientService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PATIENT')")
  public Object list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "") String q,
      @AuthenticationPrincipal Jwt jwt) {
    return service.list(page, size, q, jwt);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PATIENT')")
  public Object get(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.get(id, jwt);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object create(
      @jakarta.validation.Valid @RequestBody PatientRequest body,
      @AuthenticationPrincipal Jwt jwt) {
    return service.create(body.toMap(), jwt);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object update(
      @PathVariable UUID id,
      @jakarta.validation.Valid @RequestBody PatientRequest body,
      @AuthenticationPrincipal Jwt jwt) {
    return service.update(id, body.toMap(), jwt);
  }

  @GetMapping("/{id}/exists")
  public Object exists(@PathVariable UUID id) {
    return service.exists(id);
  }

  @GetMapping("/{id}/access")
  public Object access(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.access(id, jwt);
  }

  @PostMapping("/{id}/assignments")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object assign(
      @PathVariable UUID id, @RequestBody Map<String, Object> b, @AuthenticationPrincipal Jwt jwt) {
    return service.assign(id, b, jwt);
  }

  @PatchMapping("/me")
  @PreAuthorize("hasRole('PATIENT')")
  public Object profile(@RequestBody Map<String, Object> b, @AuthenticationPrincipal Jwt jwt) {
    return service.profile(b, jwt);
  }

  @GetMapping("/assigned-ids")
  @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
  public Object assignedIds(@AuthenticationPrincipal Jwt jwt) {
    return service.assignedIds(jwt);
  }

  @GetMapping("/references")
  @PreAuthorize(
      "hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PHARMACIST','LAB_TECHNICIAN','BILLING_STAFF','PATIENT')")
  public Object references(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "") String q,
      @AuthenticationPrincipal Jwt jwt) {
    return service.references(page, q, jwt);
  }
}
