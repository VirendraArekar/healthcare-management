package com.hospital.staff.service;

import com.hospital.common.util.Validation;
import com.hospital.staff.repository.StaffRepository;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {
  private final StaffRepository repository;

  public AvailabilityService(StaffRepository repository) {
    this.repository = repository;
  }

  public Object check(UUID doctor, Instant start, Instant end) {
    var profile = repository.get("staff", doctor);
    Validation.require("ACTIVE".equals(profile.get("status")), "Staff member is inactive");
    var from = start.atZone(ZoneOffset.UTC);
    var until = end.atZone(ZoneOffset.UTC);
    Validation.require(
        end.isAfter(start) && from.toLocalDate().equals(until.toLocalDate()),
        "Slot must be within a single UTC day");
    Validation.require(
        repository.available(doctor, from.toLocalDate(), from.toLocalTime(), until.toLocalTime()),
        "Doctor is outside working hours or on approved leave");
    return Map.of("doctorId", doctor, "available", true);
  }
}
