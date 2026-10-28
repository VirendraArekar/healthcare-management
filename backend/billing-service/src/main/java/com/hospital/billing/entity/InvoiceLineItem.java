package com.hospital.billing.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Embeddable
public class InvoiceLineItem {
  @Column(nullable = false, length = 200)
  private String description;

  @Column(nullable = false)
  private int quantity;

  @Column(name = "unit_price", nullable = false, precision = 14, scale = 2)
  private BigDecimal unitPrice;

  protected InvoiceLineItem() {}

  public InvoiceLineItem(String description, int quantity, BigDecimal price) {
    if (quantity < 1 || price.signum() < 0)
      throw new IllegalArgumentException("Invalid invoice item");
    this.description = description;
    this.quantity = quantity;
    this.unitPrice = price;
  }

  public Map<String, Object> response() {
    return Map.of(
        "description",
        description,
        "quantity",
        quantity,
        "unitPrice",
        unitPrice,
        "amount",
        unitPrice.multiply(BigDecimal.valueOf(quantity)));
  }
}
