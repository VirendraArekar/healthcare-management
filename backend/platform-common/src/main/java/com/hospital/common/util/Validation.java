package com.hospital.common.util;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class Validation {
  private Validation() {}

  public static void require(boolean condition, String message) {
    if (!condition) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
  }

  public static String required(Map<String, Object> data, String key) {
    String v = Objects.toString(data.get(key), "").trim();
    require(!v.isEmpty() && v.length() < 2000, key + " is required (max 1999 characters)");
    return v;
  }

  public static void page(int page, int size) {
    require(page >= 0 && size > 0 && size <= 100, "Invalid pagination");
  }
}
