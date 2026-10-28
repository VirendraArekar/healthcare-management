package com.hospital.common.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Service-owned document aggregate, mapped by Hibernate into each service's own records table. */
@Entity
@Table(name = "records")
public class ClinicalResource {
  @Id private UUID id;

  @Column(nullable = false, length = 60)
  private String kind;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false, columnDefinition = "jsonb")
  private Map<String, Object> data;

  @Version private long version;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected ClinicalResource() {}

  public ClinicalResource(UUID id, String kind, Map<String, Object> data) {
    this.id = id;
    this.kind = kind;
    this.data = new LinkedHashMap<>(data);
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getKind() {
    return kind;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public long getVersion() {
    return version;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setData(Map<String, Object> data) {
    this.data = new LinkedHashMap<>(data);
  }
}
