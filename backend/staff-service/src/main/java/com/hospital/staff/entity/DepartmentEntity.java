package com.hospital.staff.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "Department")
@Table(name = "departments")
public class DepartmentEntity extends BaseEntity {
  public DepartmentEntity() {}

  @Column(name = "name", nullable = false, length = 100)
  private String name = null;

  @Column(name = "description", nullable = true, columnDefinition = "text")
  private String description = null;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "ACTIVE";

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }
}
