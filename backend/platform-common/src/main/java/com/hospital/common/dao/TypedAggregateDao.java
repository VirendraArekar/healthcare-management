package com.hospital.common.dao;

import java.util.*;

public interface TypedAggregateDao {
  String kind();

  Map<String, Object> get(UUID id);

  Map<String, Object> create(Map<String, Object> data);

  Map<String, Object> update(UUID id, Map<String, Object> data, long version);

  Map<String, Object> list(
      int page, int size, String query, String owner, Set<UUID> patients, String status);
}
