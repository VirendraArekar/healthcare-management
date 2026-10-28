package com.hospital.staff.controller;

import com.hospital.staff.service.LeaveService;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/leave")
public class LeaveController {
  private final LeaveService service;

  public LeaveController(LeaveService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTIONIST')")
  public Object list(@RequestParam(defaultValue = "0") int page) {
    return service.list(page);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')")
  public Object create(@RequestBody Map<String, Object> b) {
    return service.create(b);
  }

  @PostMapping("/{id}/decision")
  @PreAuthorize("hasRole('ADMIN')")
  public Object decide(@PathVariable java.util.UUID id, @RequestBody Map<String, Object> b) {
    return service.decide(id, b);
  }
}
