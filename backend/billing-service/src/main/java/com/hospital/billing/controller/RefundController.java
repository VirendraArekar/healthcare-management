package com.hospital.billing.controller;

import com.hospital.billing.service.RefundService;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing/refunds")
public class RefundController {
  private final RefundService service;

  public RefundController(RefundService service) {
    this.service = service;
  }

  @PostMapping("/{id}/reject")
  @PreAuthorize("hasRole('ADMIN')")
  public Object reject(
      @PathVariable UUID id, @RequestBody Map<String, Object> b, @AuthenticationPrincipal Jwt jwt) {
    return service.decide(
        id, "REJECTED", Objects.toString(b.get("reason"), "Rejected by administrator"), jwt);
  }

  @PostMapping("/{id}/settle")
  @PreAuthorize("hasRole('BILLING_STAFF')")
  public Object settle(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return service.settle(id, jwt);
  }
}
