package com.hospital.reporting.controller;

import com.hospital.reporting.service.ReportingService;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF')")
public class ReportingController {
  private final ReportingService service;

  public ReportingController(ReportingService service) {
    this.service = service;
  }

  @GetMapping(value = "/pdf", produces = "application/pdf")
  public ResponseEntity<byte[]> pdf() {
    return service.pdf();
  }

  @GetMapping
  public Object stats() {
    return service.stats();
  }

  @GetMapping(value = "/export", produces = "text/csv")
  public ResponseEntity<String> csv() {
    return service.csv();
  }

  @GetMapping("/daily")
  public Object daily() {
    return service.daily();
  }

  @GetMapping("/monthly")
  public Object monthly() {
    return service.monthly();
  }

  @GetMapping("/doctor-utilization")
  public Object utilization() {
    return service.utilization();
  }

  @GetMapping("/audit")
  @PreAuthorize("hasRole('ADMIN')")
  public Object audit(
      @RequestParam(name = "service", defaultValue = "auth") String serviceName,
      @RequestParam(defaultValue = "0") int page) {
    return service.audit(serviceName, page);
  }

  @GetMapping("/health")
  @PreAuthorize("hasRole('ADMIN')")
  public Object health() {
    return service.health();
  }

  @GetMapping("/stock")
  @PreAuthorize("hasRole('ADMIN')")
  public Object stock() {
    return service.stock();
  }

  @GetMapping("/expiry")
  @PreAuthorize("hasRole('ADMIN')")
  public Object expiry() {
    return service.expiry();
  }

  @GetMapping("/balances")
  public Object balances() {
    return service.balances();
  }
}
