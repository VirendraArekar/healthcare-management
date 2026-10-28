package com.hospital.notification.controller;

import com.hospital.notification.service.NotificationService;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
  private final NotificationService service;

  public NotificationController(NotificationService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize(
      "hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','LAB_TECHNICIAN','PHARMACIST','BILLING_STAFF','PATIENT')")
  public Object list(
      @RequestParam(defaultValue = "0") int page,
      @org.springframework.security.core.annotation.AuthenticationPrincipal
          org.springframework.security.oauth2.jwt.Jwt jwt) {
    return service.list(page, jwt);
  }

  @PostMapping("/{id}/read")
  public Object read(
      @PathVariable UUID id,
      @org.springframework.security.core.annotation.AuthenticationPrincipal
          org.springframework.security.oauth2.jwt.Jwt jwt) {
    return service.read(id, jwt);
  }

  @GetMapping("/failed")
  @PreAuthorize("hasRole('ADMIN')")
  public Object failed() {
    return service.failed();
  }

  @PostMapping("/failed/{id}/retry")
  @PreAuthorize("hasRole('ADMIN')")
  public Object retry(@PathVariable UUID id) {
    return service.retry(id);
  }
}
