package com.hospital.billing.controller;

import com.hospital.billing.service.BillingPatientService;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing/my")
@PreAuthorize("hasRole('PATIENT')")
public class BillingPatientController {
  private final BillingPatientService service;

  public BillingPatientController(BillingPatientService service) {
    this.service = service;
  }

  @GetMapping
  public Object mine(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") int page) {
    return service.mine(jwt, page);
  }
}
