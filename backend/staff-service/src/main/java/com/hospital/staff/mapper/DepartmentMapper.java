package com.hospital.staff.mapper;

import com.hospital.common.mapper.*;
import com.hospital.staff.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper implements AggregateMapper<DepartmentEntity> {
  public DepartmentEntity create(Map<String, Object> data) {
    var entity = new DepartmentEntity();
    update(entity, data);
    return entity;
  }

  public void update(DepartmentEntity entity, Map<String, Object> data) {
    if (data.containsKey("name")) entity.setName(Values.text(data.get("name")));
    if (data.containsKey("description"))
      entity.setDescription(Values.text(data.get("description")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
  }

  public Map<String, Object> response(DepartmentEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getName() != null) out.put("name", entity.getName());
    if (entity.getDescription() != null) out.put("description", entity.getDescription());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    return out;
  }
}
