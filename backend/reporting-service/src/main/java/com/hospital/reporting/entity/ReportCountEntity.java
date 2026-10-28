package com.hospital.reporting.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report_counts")
public class ReportCountEntity {
  @Id
  @Column(name = "event_type", columnDefinition = "text")
  private String eventType;

  @Column(nullable = false)
  private long total;

  protected ReportCountEntity() {}

  public String getEventType() {
    return eventType;
  }

  public long getTotal() {
    return total;
  }
}
