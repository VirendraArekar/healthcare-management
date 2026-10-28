package com.hospital.lab.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "LaboratoryTest")
@Table(name = "laboratory_tests")
public class LaboratoryTestEntity extends BaseEntity {
  public LaboratoryTestEntity() {}

  @Column(name = "name", nullable = false, length = 200)
  private String name = null;

  @Column(name = "reference_range", nullable = false, length = 200)
  private String referenceRange = null;

  @Column(name = "unit", nullable = false, length = 40)
  private String unit = null;

  @Column(name = "specimen_type", nullable = false, length = 80)
  private String specimenType = "Blood";

  @Column(name = "turnaround_hours", nullable = false)
  private Integer turnaroundHours = 24;

  @Column(name = "price", nullable = false, precision = 14, scale = 2)
  private BigDecimal price = BigDecimal.ZERO;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "ACTIVE";

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
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

  public String getSpecimenType() {
    return specimenType;
  }

  public void setSpecimenType(String value) {
    this.specimenType = value;
  }

  public Integer getTurnaroundHours() {
    return turnaroundHours;
  }

  public void setTurnaroundHours(Integer value) {
    this.turnaroundHours = value;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal value) {
    this.price = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }
}
