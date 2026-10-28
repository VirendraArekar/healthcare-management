package com.hospital.notification.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "Notification")
@Table(name = "notifications")
public class NotificationEntity extends BaseEntity {
  public NotificationEntity() {}

  @Column(name = "patient_id", nullable = true)
  private UUID patientId = null;

  @Column(name = "event_type", nullable = false, length = 120)
  private String eventType = null;

  @Column(name = "aggregate_id", nullable = false)
  private UUID aggregateId = null;

  @Column(name = "message", nullable = false, columnDefinition = "text")
  private String message = null;

  @Column(name = "is_read", nullable = false)
  private Boolean read = false;

  public UUID getPatientId() {
    return patientId;
  }

  public void setPatientId(UUID value) {
    this.patientId = value;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String value) {
    this.eventType = value;
  }

  public UUID getAggregateId() {
    return aggregateId;
  }

  public void setAggregateId(UUID value) {
    this.aggregateId = value;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String value) {
    this.message = value;
  }

  public Boolean getRead() {
    return read;
  }

  public void setRead(Boolean value) {
    this.read = value;
  }
}
