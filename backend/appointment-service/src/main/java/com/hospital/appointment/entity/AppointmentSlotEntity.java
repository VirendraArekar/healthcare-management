package com.hospital.appointment.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "slots")
public class AppointmentSlotEntity extends BaseEntity {
  protected AppointmentSlotEntity() {}

  @Column(name = "doctor_id", nullable = false)
  private UUID doctorId;

  @Column(name = "starts_at", nullable = false)
  private Instant startsAt;

  @Column(name = "ends_at", nullable = false)
  private Instant endsAt;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled;

  public UUID getDoctorId() {
    return doctorId;
  }

  public Instant getStartsAt() {
    return startsAt;
  }

  public Instant getEndsAt() {
    return endsAt;
  }

  public Boolean getEnabled() {
    return enabled;
  }
}
