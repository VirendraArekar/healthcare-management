package com.hospital.staff.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "StaffLeave")
@Table(name = "staff_leave")
public class StaffLeaveEntity extends BaseEntity {
  public StaffLeaveEntity() {}

  @Column(name = "staff_id", nullable = false)
  private UUID staffId = null;

  @Column(name = "starts_on", nullable = false)
  private LocalDate startsOn = null;

  @Column(name = "ends_on", nullable = false)
  private LocalDate endsOn = null;

  @Column(name = "reason", nullable = true, columnDefinition = "text")
  private String reason = null;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "PENDING";

  @Column(name = "requested_by", nullable = true)
  private UUID requestedBy = null;

  @Column(name = "approved_by", nullable = true)
  private UUID approvedBy = null;

  public UUID getStaffId() {
    return staffId;
  }

  public void setStaffId(UUID value) {
    this.staffId = value;
  }

  public LocalDate getStartsOn() {
    return startsOn;
  }

  public void setStartsOn(LocalDate value) {
    this.startsOn = value;
  }

  public LocalDate getEndsOn() {
    return endsOn;
  }

  public void setEndsOn(LocalDate value) {
    this.endsOn = value;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String value) {
    this.reason = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }

  public UUID getRequestedBy() {
    return requestedBy;
  }

  public void setRequestedBy(UUID value) {
    this.requestedBy = value;
  }

  public UUID getApprovedBy() {
    return approvedBy;
  }

  public void setApprovedBy(UUID value) {
    this.approvedBy = value;
  }
}
