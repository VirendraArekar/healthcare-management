package com.hospital.common.repository;

import java.util.*;

/** Service repository contract; persistence mechanisms are DAO implementation details. */
public interface ResourceOperations {
  Map<String, Object> get(String kind, UUID id);

  Map<String, Object> create(String kind, Map<String, Object> data);

  Map<String, Object> update(String kind, UUID id, Map<String, Object> data, long version);

  Map<String, Object> list(String kind, int page, int size, String q, String owner);

  Map<String, Object> dto(Map<String, Object> row);

  Map<String, Object> listForPatients(
      String kind, int page, int size, String q, Set<UUID> patients);

  Map<String, Object> listByStatus(String kind, int page, int size, String owner, String status);
}
