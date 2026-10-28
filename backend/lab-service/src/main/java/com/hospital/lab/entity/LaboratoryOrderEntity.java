package com.hospital.lab.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "LaboratoryOrder")
@Table(name = "laboratory_orders")
public class LaboratoryOrderEntity extends BaseEntity {
  public LaboratoryOrderEntity() {}

  @Column(name = "patient_id", nullable = false)
  private UUID patientId = null;

  @Column(name = "test", nullable = false, length = 200)
  private String test = null;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "ORDERED";

  @Column(name = "result", nullable = true, columnDefinition = "text")
  private String result = null;

  @Column(name = "reference_range", nullable = true, length = 200)
  private String referenceRange = null;

  @Column(name = "unit", nullable = true, length = 40)
  private String unit = null;

  @Column(name = "entered_by", nullable = true)
  private UUID enteredBy = null;

  @Column(name = "verified_by", nullable = true)
  private UUID verifiedBy = null;

  public UUID getPatientId() {
    return patientId;
  }

  public void setPatientId(UUID value) {
    this.patientId = value;
  }

  public String getTest() {
    return test;
  }

  public void setTest(String value) {
    this.test = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String value) {
    this.result = value;
  }

  public String getReferenceRange() {
    return referenceRange;
  }

  public void setReferenceRange(String value) {
    this.referenceRange = value;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String value) {
    this.unit = value;
  }

  public UUID getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(UUID value) {
    this.enteredBy = value;
  }

  public UUID getVerifiedBy() {
    return verifiedBy;
  }

  public void setVerifiedBy(UUID value) {
    this.verifiedBy = value;
  }
}
