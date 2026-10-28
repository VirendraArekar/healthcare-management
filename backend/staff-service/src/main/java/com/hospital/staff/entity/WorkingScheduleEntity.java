package com.hospital.staff.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "WorkingSchedule")
@Table(name = "working_schedules")
public class WorkingScheduleEntity extends BaseEntity {
  public WorkingScheduleEntity() {}

  @Column(name = "staff_id", nullable = false)
  private UUID staffId = null;

  @Column(name = "day_of_week", nullable = false, length = 12)
  private String dayOfWeek = null;

  @Column(name = "starts_at", nullable = false)
  private LocalTime startsAt = null;

  @Column(name = "ends_at", nullable = false)
  private LocalTime endsAt = null;

  @Column(name = "timezone", nullable = false, length = 64)
  private String timezone = "UTC";

  @Column(name = "status", nullable = false, length = 30)
  private String status = "ACTIVE";

  public UUID getStaffId() {
    return staffId;
  }

  public void setStaffId(UUID value) {
    this.staffId = value;
  }

  public String getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(String value) {
    this.dayOfWeek = value;
  }

  public LocalTime getStartsAt() {
    return startsAt;
  }

  public void setStartsAt(LocalTime value) {
    this.startsAt = value;
  }

  public LocalTime getEndsAt() {
    return endsAt;
  }

  public void setEndsAt(LocalTime value) {
    this.endsAt = value;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String value) {
    this.timezone = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }
}
