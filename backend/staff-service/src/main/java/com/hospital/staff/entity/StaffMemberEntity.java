package com.hospital.staff.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "StaffMember")
@Table(name = "staff_profiles")
public class StaffMemberEntity extends BaseEntity {
  public StaffMemberEntity() {}

  @Column(name = "name", nullable = false, length = 150)
  private String name = null;

  @Column(name = "department", nullable = false, length = 100)
  private String department = null;

  @Column(name = "specialty", nullable = false, length = 100)
  private String specialty = null;

  @Column(name = "email", nullable = true, length = 254)
  private String email = null;

  @Column(name = "phone", nullable = true, length = 30)
  private String phone = null;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "ACTIVE";

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String value) {
    this.department = value;
  }

  public String getSpecialty() {
    return specialty;
  }

  public void setSpecialty(String value) {
    this.specialty = value;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String value) {
    this.phone = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }
}
