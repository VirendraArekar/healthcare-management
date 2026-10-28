package com.hospital.lab.controller;

import com.hospital.lab.dto.LaboratoryOrderRequest;
import com.hospital.lab.service.LaboratoryService;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lab")
public class LaboratoryController {
  private final LaboratoryService service;

  public LaboratoryController(LaboratoryService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','LAB_TECHNICIAN')")
  public Object list(@RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal Jwt jwt) {
    return service.list(page, jwt);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
  @ResponseStatus(HttpStatus.CREATED)
  public Object order(@jakarta.validation.Valid @RequestBody LaboratoryOrderRequest b) {
    return service.order(b.toMap());
  }

  @PostMapping("/{id}/transition")
  @PreAuthorize("hasRole('LAB_TECHNICIAN')")
  public Object transition(
      @PathVariable UUID id,
      @jakarta.validation.Valid @RequestBody Map<String, Object> b,
      @AuthenticationPrincipal Jwt jwt) {
    return service.transition(id, b, jwt);
  }

  @GetMapping("/{id}/history")
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','LAB_TECHNICIAN')")
  public Object history(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.history(id, jwt);
  }
}
