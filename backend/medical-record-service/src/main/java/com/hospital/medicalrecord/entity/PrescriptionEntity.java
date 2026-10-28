package com.hospital.medicalrecord.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "Prescription")
@Table(name = "prescriptions")
public class PrescriptionEntity extends BaseEntity {
  public PrescriptionEntity() {}

  @Column(name = "patient_id", nullable = false)
  private UUID patientId = null;

  @Column(name = "doctor_id", nullable = true)
  private UUID doctorId = null;

  @Column(name = "medicine", nullable = false, length = 200)
  private String medicine = null;

  @Column(name = "quantity", nullable = false)
  private Integer quantity = 0;

  @Column(name = "valid_until", nullable = false)
  private LocalDate validUntil = null;

  @Column(name = "instructions", nullable = true, columnDefinition = "text")
  private String instructions = null;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "ACTIVE";

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

  public String getMedicine() {
    return medicine;
  }

  public void setMedicine(String value) {
    this.medicine = value;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer value) {
    this.quantity = value;
  }

  public LocalDate getValidUntil() {
    return validUntil;
  }

  public void setValidUntil(LocalDate value) {
    this.validUntil = value;
  }

  public String getInstructions() {
    return instructions;
  }

  public void setInstructions(String value) {
    this.instructions = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }
}
