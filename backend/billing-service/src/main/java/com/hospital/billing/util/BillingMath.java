package com.hospital.billing.util;

import java.math.*;

public final class BillingMath {
  private BillingMath() {}

  public static BigDecimal total(BigDecimal subtotal, BigDecimal tax, BigDecimal discount) {
    if (subtotal.signum() < 0
        || tax.signum() < 0
        || discount.signum() < 0
        || discount.compareTo(subtotal) > 0) throw new IllegalArgumentException();
    return subtotal
        .subtract(discount)
        .multiply(BigDecimal.ONE.add(tax.divide(new BigDecimal("100"))))
        .setScale(2, RoundingMode.HALF_UP);
  }
}
