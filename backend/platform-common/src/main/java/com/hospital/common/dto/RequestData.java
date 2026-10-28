package com.hospital.common.dto;

import java.util.*;

public interface RequestData {
  default Map<String, Object> toMap() {
    return tools.jackson.databind.json.JsonMapper.builder()
        .build()
        .convertValue(this, new tools.jackson.core.type.TypeReference<Map<String, Object>>() {});
  }
}
