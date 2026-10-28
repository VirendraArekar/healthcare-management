package com.hospital.pharmacy.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "Medicine")
@Table(name = "medicines")
public class MedicineEntity extends BaseEntity {
  public MedicineEntity() {}

  @Column(name = "name", nullable = false, length = 200)
  private String name = null;

  @Column(name = "strength", nullable = false, length = 100)
  private String strength = null;

  @Column(name = "unit", nullable = false, length = 40)
  private String unit = null;

  @Column(name = "manufacturer", nullable = true, length = 150)
  private String manufacturer = null;

  @Column(name = "reorder_level", nullable = false)
  private Integer reorderLevel = 10;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "ACTIVE";

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getStrength() {
    return strength;
  }

  public void setStrength(String value) {
    this.strength = value;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String value) {
    this.unit = value;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String value) {
    this.manufacturer = value;
  }

  public Integer getReorderLevel() {
    return reorderLevel;
  }

  public void setReorderLevel(Integer value) {
    this.reorderLevel = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }
}
