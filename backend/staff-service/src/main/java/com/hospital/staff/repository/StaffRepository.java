package com.hospital.staff.repository;

import com.hospital.common.repository.ResourceOperations;

public interface StaffRepository extends ResourceOperations {

  boolean available(
      java.util.UUID staff,
      java.time.LocalDate day,
      java.time.LocalTime start,
      java.time.LocalTime end);
}
