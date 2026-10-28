package com.hospital.medicalrecord.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "Encounter")
@Table(name = "encounters")
public class EncounterEntity extends BaseEntity {
  public EncounterEntity() {}

  @Column(name = "patient_id", nullable = false)
  private UUID patientId = null;

  @Column(name = "doctor_id", nullable = true)
  private UUID doctorId = null;

  @Column(name = "diagnosis", nullable = false, columnDefinition = "text")
  private String diagnosis = null;

  @Column(name = "notes", nullable = false, columnDefinition = "text")
  private String notes = null;

  @Column(name = "treatment", nullable = false, columnDefinition = "text")
  private String treatment = null;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "OPEN";

  public UUID getPatientId() {
    return patientId;
  }

  public void setPatientId(UUID value) {
    this.patientId = value;
  }

  public UUID getDoctorId() {
    return doctorId;
  }

  public void setDoctorId(UUID value) {
    this.doctorId = value;
  }

  public String getDiagnosis() {
    return diagnosis;
  }

  public void setDiagnosis(String value) {
    this.diagnosis = value;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String value) {
    this.notes = value;
  }

  public String getTreatment() {
    return treatment;
  }

  public void setTreatment(String value) {
    this.treatment = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }
}
