package com.hospital.pharmacy.repository;

import com.hospital.common.repository.ResourceOperations;

public interface PharmacyRepository extends ResourceOperations {
  java.util.List<java.util.Map<String, Object>> findBatches();

  int insertBatch(java.util.UUID id, String medicine, int quantity, java.time.LocalDate expiry);

  int decreaseStock(int quantity, java.util.UUID id, int minimum);

  int insertMovement(
      java.util.UUID id,
      java.util.UUID batch,
      java.util.UUID prescription,
      int quantity,
      String actor);

  Integer remainingStock(java.util.UUID id);

  void initializePrescription(java.util.UUID id);

  int reservePrescription(java.util.UUID id, int qty, int maximum);

  Object movements();

  int adjust(java.util.UUID id, int delta);

  Object alerts();

  java.util.List<java.util.Map<String, Object>> findBatch(java.util.UUID id);

  Object batch(java.util.UUID id);

  int reorderLevel(java.util.UUID batch);

  boolean claimAlert(java.util.UUID batch, String type);

  java.util.List<java.util.Map<String, Object>> expiring(int days);
}
