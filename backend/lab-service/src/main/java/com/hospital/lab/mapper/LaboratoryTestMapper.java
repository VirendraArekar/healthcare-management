package com.hospital.lab.mapper;

import com.hospital.common.mapper.*;
import com.hospital.lab.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class LaboratoryTestMapper implements AggregateMapper<LaboratoryTestEntity> {
  public LaboratoryTestEntity create(Map<String, Object> data) {
    var entity = new LaboratoryTestEntity();
    update(entity, data);
    return entity;
  }

  public void update(LaboratoryTestEntity entity, Map<String, Object> data) {
    if (data.containsKey("name")) entity.setName(Values.text(data.get("name")));
    if (data.containsKey("referenceRange"))
      entity.setReferenceRange(Values.text(data.get("referenceRange")));
    if (data.containsKey("unit")) entity.setUnit(Values.text(data.get("unit")));
    if (data.get("specimenType") != null)
      entity.setSpecimenType(Values.text(data.get("specimenType")));
    if (data.get("turnaroundHours") != null)
      entity.setTurnaroundHours(Values.integer(data.get("turnaroundHours")));
    if (data.get("price") != null) entity.setPrice(Values.money(data.get("price")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
  }

  public Map<String, Object> response(LaboratoryTestEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getName() != null) out.put("name", entity.getName());
    if (entity.getReferenceRange() != null) out.put("referenceRange", entity.getReferenceRange());
    if (entity.getUnit() != null) out.put("unit", entity.getUnit());
    if (entity.getSpecimenType() != null) out.put("specimenType", entity.getSpecimenType());
    if (entity.getTurnaroundHours() != null)
      out.put("turnaroundHours", entity.getTurnaroundHours());
    if (entity.getPrice() != null) out.put("price", entity.getPrice());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    return out;
  }
}
