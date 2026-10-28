package com.hospital.patient.repository;

import com.hospital.common.repository.ResourceOperations;

public interface PatientRepository extends ResourceOperations {
  int assign(java.util.UUID patient, java.util.UUID clinician, String role);

  boolean assigned(java.util.UUID patient, java.util.UUID clinician);

  Object assignedPatients(java.util.UUID clinician, int page);

  java.util.Set<java.util.UUID> assignedPatientIds(java.util.UUID clinician);
}
