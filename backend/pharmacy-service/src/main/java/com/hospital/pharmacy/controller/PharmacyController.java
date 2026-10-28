package com.hospital.pharmacy.controller;

import com.hospital.pharmacy.dto.StockReceiptRequest;
import com.hospital.pharmacy.service.PharmacyService;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {
  private final PharmacyService service;

  public PharmacyController(PharmacyService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','DOCTOR')")
  public Object stock() {
    return service.stock();
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
  @ResponseStatus(HttpStatus.CREATED)
  public Object receive(@jakarta.validation.Valid @RequestBody StockReceiptRequest b) {
    return service.receive(b.toMap());
  }

  @PostMapping("/{id}/dispense")
  @PreAuthorize("hasRole('PHARMACIST')")
  public Object dispense(
      @PathVariable UUID id,
      @jakarta.validation.Valid @RequestBody Map<String, Object> b,
      @AuthenticationPrincipal Jwt jwt) {
    return service.dispense(id, b, jwt);
  }

  @PostMapping("/{id}/adjust")
  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
  public Object adjust(
      @PathVariable UUID id, @RequestBody Map<String, Object> b, @AuthenticationPrincipal Jwt jwt) {
    return service.adjust(id, b, jwt);
  }

  @GetMapping("/movements")
  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
  public Object movements() {
    return service.movements();
  }

  @GetMapping("/alerts")
  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
  public Object alerts() {
    return service.alerts();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','DOCTOR')")
  public Object batch(@PathVariable UUID id) {
    return service.batch(id);
  }
}
