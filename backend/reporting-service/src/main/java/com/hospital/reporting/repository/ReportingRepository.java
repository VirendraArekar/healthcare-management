package com.hospital.reporting.repository;

import com.hospital.common.repository.ResourceOperations;

public interface ReportingRepository extends ResourceOperations {
  int claimEvent(Object... args);

  int projectEvent(Object... args);

  java.util.List<java.util.Map<String, Object>> findReportCounts(Object... args);

  java.util.List<java.util.Map<String, Object>> findCsvReportCounts(Object... args);

  java.util.List<java.util.Map<String, Object>> findPdfReportCounts(Object... args);

  void daily(java.time.LocalDate day, String type, java.math.BigDecimal amount);

  void doctor(java.util.UUID id);

  Object daily();

  Object monthly();

  Object utilization();
}
