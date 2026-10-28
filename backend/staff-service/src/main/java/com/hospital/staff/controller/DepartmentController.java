package com.hospital.staff.controller;

import com.hospital.staff.service.DepartmentService;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
  private final DepartmentService service;

  public DepartmentController(DepartmentService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
  public Object list() {
    return service.list();
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Object create(@jakarta.validation.Valid @RequestBody Map<String, Object> b) {
    return service.create(b);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public Object update(@PathVariable java.util.UUID id, @RequestBody Map<String, Object> b) {
    return service.update(id, b);
  }
}
