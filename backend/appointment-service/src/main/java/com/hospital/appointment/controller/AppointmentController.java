package com.hospital.appointment.controller;

import com.hospital.appointment.dto.BookingRequest;
import com.hospital.appointment.dto.SlotRequest;
import com.hospital.appointment.service.AppointmentService;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
  private final AppointmentService service;

  public AppointmentController(AppointmentService service) {
    this.service = service;
  }

  @PostMapping("/slots")
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
  public Object slot(@jakarta.validation.Valid @RequestBody SlotRequest b) {
    return service.slot(b.toMap());
  }

  @GetMapping("/slots")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','PATIENT')")
  public Object slots() {
    return service.slots();
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PATIENT')")
  public Object list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @AuthenticationPrincipal Jwt jwt) {
    return service.list(page, size, jwt);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object book(
      @jakarta.validation.Valid @RequestBody BookingRequest b, @AuthenticationPrincipal Jwt jwt) {
    return service.book(b.toMap(), jwt);
  }

  @PostMapping("/{id}/cancel")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','PATIENT')")
  public Object cancel(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.cancel(id, jwt);
  }

  @PostMapping("/{id}/reschedule")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object reschedule(
      @PathVariable UUID id, @jakarta.validation.Valid @RequestBody Map<String, Object> b) {
    return service.reschedule(id, b);
  }

  @PostMapping("/{id}/complete")
  @PreAuthorize("hasRole('DOCTOR')")
  public Object complete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.complete(id, jwt);
  }

  @PostMapping("/requests")
  @PreAuthorize("hasRole('PATIENT')")
  public Object request(@RequestBody Map<String, Object> b, @AuthenticationPrincipal Jwt jwt) {
    return service.request(b, jwt);
  }

  @GetMapping("/requests")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','PATIENT')")
  public Object requests(
      @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal Jwt jwt) {
    return service.requests(page, jwt);
  }

  @PostMapping("/requests/{id}/approve")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object approve(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.approve(id, jwt);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PATIENT')")
  public Object detail(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.detail(id, jwt);
  }

  @GetMapping("/utilization")
  @PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF')")
  public Object utilization(
      @RequestParam(required = false) java.time.LocalDate from,
      @RequestParam(required = false) java.time.LocalDate until) {
    var start = from == null ? java.time.LocalDate.now(java.time.ZoneOffset.UTC) : from;
    return service.utilization(start, until == null ? start.plusDays(30) : until);
  }
}
