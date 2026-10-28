package com.hospital.appointment.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "bookings")
public class AppointmentEntity extends BaseEntity {
  protected AppointmentEntity() {}

  @Column(name = "slot_id", nullable = false)
  private UUID slotId;

  @Column(name = "patient_id", nullable = false)
  private UUID patientId;

  @Column(name = "status", nullable = false, columnDefinition = "text")
  private String status;

  @Column(name = "reminded_at", nullable = true)
  private Instant remindedAt;

  @Column(name = "cancellation_reason", nullable = true, columnDefinition = "text")
  private String cancellationReason;

  public UUID getSlotId() {
    return slotId;
  }

  public UUID getPatientId() {
    return patientId;
  }

  public String getStatus() {
    return status;
  }

  public Instant getRemindedAt() {
    return remindedAt;
  }

  public String getCancellationReason() {
    return cancellationReason;
  }
}
