package com.hospital.lab.controller;

import com.hospital.lab.service.TestCatalogService;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lab/catalog")
public class TestCatalogController {
  private final TestCatalogService service;

  public TestCatalogController(TestCatalogService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','LAB_TECHNICIAN')")
  public Object list(@RequestParam(defaultValue = "0") int page) {
    return service.list(page);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','LAB_TECHNICIAN')")
  public Object create(@RequestBody Map<String, Object> b) {
    return service.create(b);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','LAB_TECHNICIAN')")
  public Object update(@PathVariable java.util.UUID id, @RequestBody Map<String, Object> b) {
    return service.update(id, b);
  }
}
