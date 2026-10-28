package com.hospital.common.mapper;

import com.hospital.common.entity.BaseEntity;
import java.util.*;

public interface AggregateMapper<T extends BaseEntity> {
  T create(Map<String, Object> data);

  void update(T entity, Map<String, Object> data);

  Map<String, Object> response(T entity);
}
