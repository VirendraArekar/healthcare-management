package com.hospital.billing.controller;

import com.hospital.billing.dto.InvoiceRequest;
import com.hospital.billing.service.BillingService;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing")
public class BillingController {
  private final BillingService service;

  public BillingController(BillingService service) {
    this.service = service;
  }

  @GetMapping(value = "/{id}/pdf", produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF','PATIENT')")
  public ResponseEntity<byte[]> pdf(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.pdf(id, jwt);
  }

  @PostMapping("/{id}/refund-request")
  @PreAuthorize("hasRole('BILLING_STAFF')")
  public Object refund(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.refund(id, jwt);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF')")
  public Object list(@RequestParam(defaultValue = "0") int page) {
    return service.list(page);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('BILLING_STAFF')")
  public Object invoice(@jakarta.validation.Valid @RequestBody InvoiceRequest b) {
    return service.invoice(b.toMap());
  }

  @PostMapping("/{id}/pay")
  @PreAuthorize("hasRole('BILLING_STAFF')")
  public Object pay(@PathVariable UUID id, @RequestHeader("Idempotency-Key") UUID key) {
    return service.pay(id, key);
  }

  @GetMapping("/refunds")
  @PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF')")
  public Object refunds(@RequestParam(defaultValue = "0") int page) {
    return service.refunds(page);
  }

  @PostMapping("/refunds/{id}/approve")
  @PreAuthorize("hasRole('ADMIN')")
  public Object approveRefund(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.approveRefund(id, jwt);
  }

  @GetMapping("/payments")
  @PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF')")
  public Object payments() {
    return service.payments();
  }

  @GetMapping("/balances")
  @PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF')")
  public Object balances() {
    return service.balances();
  }
}
