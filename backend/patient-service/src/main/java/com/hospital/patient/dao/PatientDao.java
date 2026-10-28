package com.hospital.patient.dao;

import com.hospital.common.repository.RecordRepository;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.JsonCodec;
import com.hospital.patient.repository.PatientRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PatientDao extends RecordRepository implements PatientRepository {
  public PatientDao(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    super(db, codec, jpa);
  }

  public int assign(java.util.UUID patient, java.util.UUID clinician, String role) {
    return db.update(
        "insert into care_assignments(patient_id,clinician_id,role) values(?,?,?) on conflict do"
            + " nothing",
        patient,
        clinician,
        role);
  }

  public boolean assigned(java.util.UUID patient, java.util.UUID clinician) {
    return db.queryForObject(
            "select count(*) from care_assignments where patient_id=? and clinician_id=?",
            Long.class,
            patient,
            clinician)
        > 0;
  }

  public java.util.Set<java.util.UUID> assignedPatientIds(java.util.UUID clinician) {
    return new java.util.LinkedHashSet<>(
        db.queryForList(
            "select patient_id from care_assignments where clinician_id=?",
            java.util.UUID.class,
            clinician));
  }

  public Object assignedPatients(java.util.UUID clinician, int page) {
    return listForPatients("patient", page, 20, "", assignedPatientIds(clinician));
  }
}
