package com.hospital.medicalrecord.controller;

import com.hospital.medicalrecord.dto.EncounterRequest;
import com.hospital.medicalrecord.dto.PrescriptionRequest;
import com.hospital.medicalrecord.dto.VitalSignsRequest;
import com.hospital.medicalrecord.service.MedicalRecordService;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {
  private final MedicalRecordService service;

  public MedicalRecordController(MedicalRecordService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PATIENT')")
  public Object list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "") String q,
      @AuthenticationPrincipal Jwt jwt) {
    return service.list(page, size, q, jwt);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PATIENT')")
  public Object get(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.get(id, jwt);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('DOCTOR')")
  public Object create(
      @jakarta.validation.Valid @RequestBody EncounterRequest body,
      @AuthenticationPrincipal Jwt jwt) {
    return service.create(body.toMap(), jwt);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('DOCTOR')")
  public Object update(
      @PathVariable UUID id,
      @jakarta.validation.Valid @RequestBody Map<String, Object> body,
      @AuthenticationPrincipal Jwt jwt) {
    return service.update(id, body, jwt);
  }

  @PostMapping("/{id}/vitals")
  @PreAuthorize("hasAnyRole('NURSE','DOCTOR')")
  public Object vitals(
      @PathVariable UUID id,
      @jakarta.validation.Valid @RequestBody VitalSignsRequest body,
      @AuthenticationPrincipal Jwt jwt) {
    return service.vitals(id, body.toMap(), jwt);
  }

  @GetMapping("/{id}/history")
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PATIENT')")
  public Object history(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.history(id, jwt);
  }

  @GetMapping("/{id}/vitals")
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PATIENT')")
  public Object observations(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.observations(id, jwt);
  }

  @PostMapping("/prescriptions")
  @PreAuthorize("hasRole('DOCTOR')")
  public Object prescribe(
      @jakarta.validation.Valid @RequestBody PrescriptionRequest b,
      @AuthenticationPrincipal Jwt jwt) {
    return service.prescribe(b.toMap(), jwt);
  }

  @GetMapping("/prescriptions/{id}")
  @PreAuthorize("hasAnyRole('DOCTOR','PHARMACIST','PATIENT')")
  public Object prescription(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.prescription(id, jwt);
  }

  @GetMapping("/prescriptions")
  @PreAuthorize("hasAnyRole('DOCTOR','PHARMACIST','PATIENT')")
  public Object prescriptions(
      @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal Jwt jwt) {
    return service.prescriptions(page, jwt);
  }

  @PostMapping("/{id}/close")
  @PreAuthorize("hasRole('DOCTOR')")
  public Object close(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.close(id, jwt);
  }
}
