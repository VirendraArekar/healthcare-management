package com.hospital.pharmacy.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.pharmacy.repository.PharmacyRepository;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PharmacyService extends DomainServiceSupport {
  private final PharmacyRepository repository;

  public PharmacyService(
      PharmacyRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','DOCTOR')")
  public Object stock() {
    return Map.of("content", repository.findBatches());
  }

  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
  public Object receive(Map<String, Object> b) {
    UUID id = UUID.randomUUID();
    String medicine = Validation.required(b, "medicine");
    int quantity = Integer.parseInt(Validation.required(b, "quantity"));
    Validation.require(quantity > 0, "Positive quantity required");
    LocalDate expiry = LocalDate.parse(Validation.required(b, "expiresOn"));
    Validation.require(expiry.isAfter(LocalDate.now()), "Expired batch");
    repository.insertBatch(id, medicine, quantity, expiry);
    return Map.of("id", id);
  }

  @PreAuthorize("hasRole('PHARMACIST')")
  @Transactional
  public Object dispense(UUID id, Map<String, Object> b, Jwt jwt) {
    int qty = Integer.parseInt(Validation.required(b, "quantity"));
    Validation.require(qty > 0, "Positive quantity required");
    UUID prescription = UUID.fromString(Validation.required(b, "prescriptionId"));
    var prescribed = directory.prescription(prescription);
    Validation.require(
        "ACTIVE".equals(prescribed.get("status"))
            && !LocalDate.parse(prescribed.get("validUntil").toString()).isBefore(LocalDate.now()),
        "Prescription inactive or expired");
    var batch = repository.findBatch(id);
    Validation.require(
        !batch.isEmpty()
            && batch
                .getFirst()
                .get("medicine")
                .toString()
                .equalsIgnoreCase(prescribed.get("medicine").toString()),
        "Batch does not match the prescribed medicine");
    repository.initializePrescription(prescription);
    Validation.require(
        repository.reservePrescription(
                prescription, qty, Integer.parseInt(prescribed.get("quantity").toString()))
            == 1,
        "Prescription quantity exceeded");
    int n = repository.decreaseStock(qty, id, qty);
    Validation.require(n == 1, "Insufficient or expired stock");
    repository.insertMovement(UUID.randomUUID(), id, prescription, -qty, jwt.getSubject());
    events.publish(
        "pharmacy.medicine-dispensed.v1", id, Map.of("batchId", id.toString(), "quantity", qty));
    Integer left = repository.remainingStock(id);
    if (left <= repository.reorderLevel(id) && repository.claimAlert(id, "LOW_STOCK"))
      events.publish(
          "pharmacy.stock-low.v1", id, Map.of("batchId", id.toString(), "remaining", left));
    return Map.of("remaining", left);
  }

  @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
  public Object adjust(UUID id, Map<String, Object> b, Jwt jwt) {
    int delta = Integer.parseInt(Validation.required(b, "delta"));
    Validation.require(delta != 0, "Nonzero adjustment required");
    Validation.required(b, "reason");
    Validation.require(repository.adjust(id, delta) == 1, "Invalid batch or stock adjustment");
    repository.insertMovement(UUID.randomUUID(), id, null, delta, jwt.getSubject());
    audit.record(jwt.getSubject(), "STOCK_ADJUSTMENT: " + b.get("reason"), id);
    return Map.of("remaining", repository.remainingStock(id));
  }

  public Object movements() {
    return repository.movements();
  }

  public Object alerts() {
    return repository.alerts();
  }

  public Object batch(UUID id) {
    return repository.batch(id);
  }
}
