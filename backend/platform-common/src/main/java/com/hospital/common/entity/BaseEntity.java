package com.hospital.common.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@MappedSuperclass
public abstract class BaseEntity {
  @Id protected UUID id;
  @Version protected long version;

  @Column(name = "created_at", nullable = false, updatable = false)
  protected Instant createdAt;

  public UUID getId() {
    return id;
  }

  public long getVersion() {
    return version;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void initialize() {
    if (id == null) id = UUID.randomUUID();
    if (createdAt == null) createdAt = Instant.now();
  }
}
