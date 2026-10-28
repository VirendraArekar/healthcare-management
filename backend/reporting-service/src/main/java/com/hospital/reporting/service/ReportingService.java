package com.hospital.reporting.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.reporting.repository.ReportingRepository;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF')")
@Service
@Transactional
public class ReportingService extends DomainServiceSupport {
  private final ReportingRepository repository;

  public ReportingService(
      ReportingRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  public ResponseEntity<byte[]> pdf() {
    var lines =
        repository.findReportCounts().stream()
            .map(r -> r.get("event_type") + ": " + r.get("total"))
            .toList();
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=report.pdf")
        .body(
            com.hospital.common.util.SimplePdf.render(
                "Workflow analytics (eventually consistent)", lines));
  }

  public Object stats() {
    return Map.of("content", repository.findCsvReportCounts(), "consistency", "eventual");
  }

  public ResponseEntity<String> csv() {
    var out = new StringBuilder("event_type,total\n");
    for (var r : repository.findPdfReportCounts())
      out.append(r.get("event_type")).append(',').append(r.get("total")).append('\n');
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=workflow-report.csv")
        .body(out.toString());
  }

  public Object daily() {
    return repository.daily();
  }

  public Object monthly() {
    return repository.monthly();
  }

  public Object utilization() {
    return directory.appointmentUtilization();
  }

  public Object audit(String service, int page) {
    com.hospital.common.util.Validation.page(page, 20);
    return directory.audit(service, page);
  }

  public Object health() {
    var result = new java.util.LinkedHashMap<String, Object>();
    try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
      var context = org.springframework.security.core.context.SecurityContextHolder.getContext();
      var futures =
          new java.util.LinkedHashMap<String, java.util.concurrent.Future<Map<String, Object>>>();
      for (String service :
          List.of(
              "auth",
              "patient",
              "staff",
              "appointment",
              "medical-record",
              "lab",
              "pharmacy",
              "billing",
              "notification",
              "reporting")) {
        futures.put(
            service,
            executor.submit(
                () -> {
                  org.springframework.security.core.context.SecurityContextHolder.setContext(
                      context);
                  try {
                    return directory.health(service);
                  } finally {
                    org.springframework.security.core.context.SecurityContextHolder.clearContext();
                  }
                }));
      }
      for (var entry : futures.entrySet()) {
        try {
          result.put(
              entry.getKey(), entry.getValue().get(4, java.util.concurrent.TimeUnit.SECONDS));
        } catch (Exception ex) {
          result.put(entry.getKey(), Map.of("status", "DOWN"));
        }
      }
    }
    return result;
  }

  @PreAuthorize("hasRole('ADMIN')")
  public Object stock() {
    return directory.stockReport();
  }

  @PreAuthorize("hasRole('ADMIN')")
  public Object expiry() {
    return directory.expiryReport();
  }

  public Object balances() {
    return directory.balancesReport();
  }
}
