package com.hospital.billing.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "Refund")
@Table(name = "refunds")
public class RefundEntity extends BaseEntity {
  public RefundEntity() {}

  @Column(name = "invoice_id", nullable = false)
  private UUID invoiceId = null;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "REQUESTED";

  @Column(name = "requested_by", nullable = false)
  private UUID requestedBy = null;

  @Column(name = "approved_by", nullable = true)
  private UUID approvedBy = null;

  @Column(name = "reason", nullable = true, columnDefinition = "text")
  private String reason = null;

  @Column(name = "amount", nullable = false, precision = 14, scale = 2)
  private BigDecimal amount = BigDecimal.ZERO;

  public UUID getInvoiceId() {
    return invoiceId;
  }

  public void setInvoiceId(UUID value) {
    this.invoiceId = value;
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

  public String getReason() {
    return reason;
  }

  public void setReason(String value) {
    this.reason = value;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal value) {
    this.amount = value;
  }
}
