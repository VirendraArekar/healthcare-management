package com.hospital.staff.controller;

import com.hospital.staff.service.ScheduleService;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/schedules")
public class ScheduleController {
  private final ScheduleService service;

  public ScheduleController(ScheduleService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTIONIST')")
  public Object list(@RequestParam(defaultValue = "0") int page) {
    return service.list(page);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
  public Object create(@RequestBody Map<String, Object> b) {
    return service.create(b);
  }
}
