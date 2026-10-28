package com.hospital.billing.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "Invoice")
@Table(name = "invoices")
public class InvoiceEntity extends BaseEntity {
  public InvoiceEntity() {}

  @Column(name = "patient_id", nullable = false)
  private UUID patientId = null;

  @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
  private BigDecimal subtotal = BigDecimal.ZERO;

  @Column(name = "tax_percent", nullable = false, precision = 7, scale = 4)
  private BigDecimal taxPercent = BigDecimal.ZERO;

  @Column(name = "discount", nullable = false, precision = 14, scale = 2)
  private BigDecimal discount = BigDecimal.ZERO;

  @Column(name = "total", nullable = false, precision = 14, scale = 2)
  private BigDecimal total = BigDecimal.ZERO;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "UNPAID";

  @Column(name = "currency", nullable = false, length = 3)
  private String currency = "USD";

  public UUID getPatientId() {
    return patientId;
  }

  public void setPatientId(UUID value) {
    this.patientId = value;
  }

  public BigDecimal getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(BigDecimal value) {
    this.subtotal = value;
  }

  public BigDecimal getTaxPercent() {
    return taxPercent;
  }

  public void setTaxPercent(BigDecimal value) {
    this.taxPercent = value;
  }

  public BigDecimal getDiscount() {
    return discount;
  }

  public void setDiscount(BigDecimal value) {
    this.discount = value;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal value) {
    this.total = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String value) {
    this.currency = value;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "invoice_line_items", joinColumns = @JoinColumn(name = "invoice_id"))
  @OrderColumn(name = "line_index")
  private java.util.List<InvoiceLineItem> lineItems = new ArrayList<>();

  public java.util.List<InvoiceLineItem> getLineItems() {
    return lineItems;
  }

  public void setLineItems(java.util.List<InvoiceLineItem> value) {
    this.lineItems = value;
  }
}
