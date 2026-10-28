package com.hospital.pharmacy.mapper;

import com.hospital.common.mapper.*;
import com.hospital.pharmacy.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class MedicineMapper implements AggregateMapper<MedicineEntity> {
  public MedicineEntity create(Map<String, Object> data) {
    var entity = new MedicineEntity();
    update(entity, data);
    return entity;
  }

  public void update(MedicineEntity entity, Map<String, Object> data) {
    if (data.containsKey("name")) entity.setName(Values.text(data.get("name")));
    if (data.containsKey("strength")) entity.setStrength(Values.text(data.get("strength")));
    if (data.containsKey("unit")) entity.setUnit(Values.text(data.get("unit")));
    if (data.containsKey("manufacturer"))
      entity.setManufacturer(Values.text(data.get("manufacturer")));
    if (data.get("reorderLevel") != null)
      entity.setReorderLevel(Values.integer(data.get("reorderLevel")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
  }

  public Map<String, Object> response(MedicineEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getName() != null) out.put("name", entity.getName());
    if (entity.getStrength() != null) out.put("strength", entity.getStrength());
    if (entity.getUnit() != null) out.put("unit", entity.getUnit());
    if (entity.getManufacturer() != null) out.put("manufacturer", entity.getManufacturer());
    if (entity.getReorderLevel() != null) out.put("reorderLevel", entity.getReorderLevel());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    return out;
  }
}
