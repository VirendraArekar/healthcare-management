package com.hospital.lab.repository;

import com.hospital.common.repository.ResourceOperations;

public interface LaboratoryRepository extends ResourceOperations {
  Object findPatientRecords(String owner, int page);

  void sample(java.util.UUID order, java.util.UUID actor);

  void transitionHistory(java.util.UUID order, String previous, String next, java.util.UUID actor);

  Object orderHistory(java.util.UUID order);

  java.util.List<java.util.Map<String, Object>> findTest(String name);
}
