package com.hospital.lab.controller;

import com.hospital.lab.service.LaboratoryPatientService;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lab/my")
@PreAuthorize("hasRole('PATIENT')")
public class LaboratoryPatientController {
  private final LaboratoryPatientService service;

  public LaboratoryPatientController(LaboratoryPatientService service) {
    this.service = service;
  }

  @GetMapping
  public Object mine(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") int page) {
    return service.mine(jwt, page);
  }
}
