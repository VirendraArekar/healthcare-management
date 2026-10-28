package com.hospital.common.util;

import java.util.*;
import org.springframework.stereotype.Component;
import tools.jackson.databind.*;

@Component
public class JsonCodec {
  private final ObjectMapper mapper;

  public JsonCodec(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public String json(Object value) {
    try {
      return mapper.writeValueAsString(value);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JSON", e);
    }
  }

  public Map<String, Object> object(String value) {
    try {
      return mapper.readValue(
          value, new tools.jackson.core.type.TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JSON", e);
    }
  }

  public Map<String, Object> map(Object value) {
    return mapper.convertValue(
        value, new tools.jackson.core.type.TypeReference<Map<String, Object>>() {});
  }
}
