package com.hospital.billing;

import static org.junit.jupiter.api.Assertions.*;

import com.hospital.billing.util.BillingMath;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BillingMathTest {
  @Test
  void totalsUseDecimalRounding() {
    assertEquals(
        new BigDecimal("99.00"),
        BillingMath.total(new BigDecimal("100"), new BigDecimal("10"), new BigDecimal("10")));
  }

  @Test
  void rejectsNegativeMoney() {
    assertThrows(
        IllegalArgumentException.class,
        () -> BillingMath.total(new BigDecimal("-1"), BigDecimal.ZERO, BigDecimal.ZERO));
  }

  @Test
  void rejectsExcessDiscount() {
    assertThrows(
        IllegalArgumentException.class,
        () -> BillingMath.total(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.TEN));
  }

  @Test
  void roundsHalfUp() {
    assertEquals(
        new BigDecimal("1.01"),
        BillingMath.total(new BigDecimal("1.005"), BigDecimal.ZERO, BigDecimal.ZERO));
  }
}
