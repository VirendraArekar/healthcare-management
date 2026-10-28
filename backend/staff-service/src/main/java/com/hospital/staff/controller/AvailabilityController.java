package com.hospital.staff.controller;

import com.hospital.staff.service.AvailabilityService;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
public class AvailabilityController {
  private final AvailabilityService service;

  public AvailabilityController(AvailabilityService service) {
    this.service = service;
  }

  @GetMapping("/{id}/availability")
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTIONIST','PATIENT')")
  public Object check(
      @PathVariable UUID id, @RequestParam Instant startsAt, @RequestParam Instant endsAt) {
    return service.check(id, startsAt, endsAt);
  }
}
