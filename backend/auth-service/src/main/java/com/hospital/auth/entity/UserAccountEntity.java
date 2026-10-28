package com.hospital.auth.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
public class UserAccountEntity extends BaseEntity {
  @Column(nullable = false, length = 254)
  private String email;

  @Column(name = "password_hash", nullable = false, columnDefinition = "text")
  private String passwordHash;

  @Column(nullable = false, length = 30)
  private String role;

  @Column(nullable = false)
  private boolean enabled;

  @Column(name = "patient_id")
  private UUID patientId;

  @Column(name = "staff_id")
  private UUID staffId;

  @Column(name = "failed_attempts", nullable = false)
  private int failedAttempts;

  @Column(name = "locked_until")
  private Instant lockedUntil;

  protected UserAccountEntity() {}

  public String getEmail() {
    return email;
  }

  public String getRole() {
    return role;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public UUID getPatientId() {
    return patientId;
  }

  public UUID getStaffId() {
    return staffId;
  }
}
