package com.hospital.medicalrecord.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "VitalSign")
@Table(name = "vital_signs")
public class VitalSignEntity extends BaseEntity {
  public VitalSignEntity() {}

  @Column(name = "encounter_id", nullable = false)
  private UUID encounterId = null;

  @Column(name = "observation", nullable = false, columnDefinition = "text")
  private String observation = null;

  @Column(name = "recorded_by", nullable = false)
  private UUID recordedBy = null;

  @Column(name = "systolic", nullable = true, precision = 7, scale = 2)
  private BigDecimal systolic = null;

  @Column(name = "diastolic", nullable = true, precision = 7, scale = 2)
  private BigDecimal diastolic = null;

  @Column(name = "pulse", nullable = true, precision = 7, scale = 2)
  private BigDecimal pulse = null;

  @Column(name = "temperature", nullable = true, precision = 7, scale = 2)
  private BigDecimal temperature = null;

  @Column(name = "oxygen_saturation", nullable = true, precision = 7, scale = 2)
  private BigDecimal oxygenSaturation = null;

  @Column(name = "weight", nullable = true, precision = 7, scale = 2)
  private BigDecimal weight = null;

  public UUID getEncounterId() {
    return encounterId;
  }

  public void setEncounterId(UUID value) {
    this.encounterId = value;
  }

  public String getObservation() {
    return observation;
  }

  public void setObservation(String value) {
    this.observation = value;
  }

  public UUID getRecordedBy() {
    return recordedBy;
  }

  public void setRecordedBy(UUID value) {
    this.recordedBy = value;
  }

  public BigDecimal getSystolic() {
    return systolic;
  }

  public void setSystolic(BigDecimal value) {
    this.systolic = value;
  }

  public BigDecimal getDiastolic() {
    return diastolic;
  }

  public void setDiastolic(BigDecimal value) {
    this.diastolic = value;
  }

  public BigDecimal getPulse() {
    return pulse;
  }

  public void setPulse(BigDecimal value) {
    this.pulse = value;
  }

  public BigDecimal getTemperature() {
    return temperature;
  }

  public void setTemperature(BigDecimal value) {
    this.temperature = value;
  }

  public BigDecimal getOxygenSaturation() {
    return oxygenSaturation;
  }

  public void setOxygenSaturation(BigDecimal value) {
    this.oxygenSaturation = value;
  }

  public BigDecimal getWeight() {
    return weight;
  }

  public void setWeight(BigDecimal value) {
    this.weight = value;
  }
}
