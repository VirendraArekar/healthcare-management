package com.hospital.lab.mapper;

import com.hospital.common.mapper.*;
import com.hospital.lab.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class LaboratoryOrderMapper implements AggregateMapper<LaboratoryOrderEntity> {
  public LaboratoryOrderEntity create(Map<String, Object> data) {
    var entity = new LaboratoryOrderEntity();
    update(entity, data);
    return entity;
  }

  public void update(LaboratoryOrderEntity entity, Map<String, Object> data) {
    if (data.containsKey("patientId")) entity.setPatientId(Values.uuid(data.get("patientId")));
    if (data.containsKey("test")) entity.setTest(Values.text(data.get("test")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
    if (data.containsKey("result")) entity.setResult(Values.text(data.get("result")));
    if (data.containsKey("referenceRange"))
      entity.setReferenceRange(Values.text(data.get("referenceRange")));
    if (data.containsKey("unit")) entity.setUnit(Values.text(data.get("unit")));
    if (data.containsKey("enteredBy")) entity.setEnteredBy(Values.uuid(data.get("enteredBy")));
    if (data.containsKey("verifiedBy")) entity.setVerifiedBy(Values.uuid(data.get("verifiedBy")));
  }

  public Map<String, Object> response(LaboratoryOrderEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getPatientId() != null) out.put("patientId", entity.getPatientId().toString());
    if (entity.getTest() != null) out.put("test", entity.getTest());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    if (entity.getResult() != null) out.put("result", entity.getResult());
    if (entity.getReferenceRange() != null) out.put("referenceRange", entity.getReferenceRange());
    if (entity.getUnit() != null) out.put("unit", entity.getUnit());
    if (entity.getEnteredBy() != null) out.put("enteredBy", entity.getEnteredBy().toString());
    if (entity.getVerifiedBy() != null) out.put("verifiedBy", entity.getVerifiedBy().toString());
    return out;
  }
}
