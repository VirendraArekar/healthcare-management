package com.hospital.pharmacy.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "batches")
public class MedicineBatchEntity extends BaseEntity {
  @Column(nullable = false, columnDefinition = "text")
  private String medicine;

  @Column(name = "medicine_id", nullable = false)
  private UUID medicineId;

  @Column(nullable = false)
  private int quantity;

  @Column(name = "expires_on", nullable = false)
  private LocalDate expiresOn;

  @Column(name = "batch_number", nullable = false, length = 64)
  private String batchNumber;

  protected MedicineBatchEntity() {}

  public String getMedicine() {
    return medicine;
  }

  public UUID getMedicineId() {
    return medicineId;
  }

  public int getQuantity() {
    return quantity;
  }

  public LocalDate getExpiresOn() {
    return expiresOn;
  }

  public String getBatchNumber() {
    return batchNumber;
  }
}
