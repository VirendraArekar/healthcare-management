package com.hospital.pharmacy.controller;

import com.hospital.pharmacy.service.MedicineService;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pharmacy/catalog")
public class MedicineController {
  private final MedicineService service;

  public MedicineController(MedicineService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PHARMACIST')")
  public Object list(@RequestParam(defaultValue = "0") int page) {
    return service.list(page);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
  public Object create(@RequestBody Map<String, Object> b) {
    return service.create(b);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
  public Object update(@PathVariable java.util.UUID id, @RequestBody Map<String, Object> b) {
    return service.update(id, b);
  }
}
