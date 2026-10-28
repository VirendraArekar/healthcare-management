package com.hospital.medicalrecord.repository;

import com.hospital.common.repository.ResourceOperations;

public interface MedicalRecordRepository extends ResourceOperations {
  int insertRecordHistory(
      java.util.UUID id, java.util.UUID record, Object version, String data, String actor);

  Object history(java.util.UUID id);

  Object observations(java.util.UUID id);
}
