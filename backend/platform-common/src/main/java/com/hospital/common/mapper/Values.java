package com.hospital.common.mapper;

import java.math.*;
import java.time.*;
import java.util.*;

public final class Values {
  private Values() {}

  public static String text(Object value) {
    return value == null ? null : value.toString();
  }

  public static UUID uuid(Object value) {
    return value == null || value.toString().isBlank() ? null : UUID.fromString(value.toString());
  }

  public static LocalDate date(Object value) {
    return value == null ? null : LocalDate.parse(value.toString());
  }

  public static BigDecimal money(Object value) {
    return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
  }

  public static String string(Object value, String fallback) {
    return value == null ? fallback : value.toString();
  }

  public static Boolean bool(Object value) {
    return value != null && Boolean.parseBoolean(value.toString());
  }

  public static Integer integer(Object value) {
    return value == null ? null : Integer.valueOf(value.toString());
  }

  public static LocalTime time(Object value) {
    return value == null ? null : LocalTime.parse(value.toString());
  }
}
