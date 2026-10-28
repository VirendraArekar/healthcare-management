package com.hospital.medicalrecord.mapper;

import com.hospital.common.mapper.*;
import com.hospital.medicalrecord.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class VitalSignMapper implements AggregateMapper<VitalSignEntity> {
  public VitalSignEntity create(Map<String, Object> data) {
    var entity = new VitalSignEntity();
    update(entity, data);
    return entity;
  }

  public void update(VitalSignEntity entity, Map<String, Object> data) {
    if (data.containsKey("encounterId"))
      entity.setEncounterId(Values.uuid(data.get("encounterId")));
    if (data.containsKey("observation"))
      entity.setObservation(Values.text(data.get("observation")));
    if (data.containsKey("recordedBy")) entity.setRecordedBy(Values.uuid(data.get("recordedBy")));
    if (data.containsKey("systolic")) entity.setSystolic(Values.money(data.get("systolic")));
    if (data.containsKey("diastolic")) entity.setDiastolic(Values.money(data.get("diastolic")));
    if (data.containsKey("pulse")) entity.setPulse(Values.money(data.get("pulse")));
    if (data.containsKey("temperature"))
      entity.setTemperature(Values.money(data.get("temperature")));
    if (data.containsKey("oxygenSaturation"))
      entity.setOxygenSaturation(Values.money(data.get("oxygenSaturation")));
    if (data.containsKey("weight")) entity.setWeight(Values.money(data.get("weight")));
  }

  public Map<String, Object> response(VitalSignEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getEncounterId() != null) out.put("encounterId", entity.getEncounterId().toString());
    if (entity.getObservation() != null) out.put("observation", entity.getObservation());
    if (entity.getRecordedBy() != null) out.put("recordedBy", entity.getRecordedBy().toString());
    if (entity.getSystolic() != null) out.put("systolic", entity.getSystolic());
    if (entity.getDiastolic() != null) out.put("diastolic", entity.getDiastolic());
    if (entity.getPulse() != null) out.put("pulse", entity.getPulse());
    if (entity.getTemperature() != null) out.put("temperature", entity.getTemperature());
    if (entity.getOxygenSaturation() != null)
      out.put("oxygenSaturation", entity.getOxygenSaturation());
    if (entity.getWeight() != null) out.put("weight", entity.getWeight());
    return out;
  }
}
