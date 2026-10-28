package com.hospital.medicalrecord.mapper;

import com.hospital.common.mapper.*;
import com.hospital.medicalrecord.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper implements AggregateMapper<PrescriptionEntity> {
  public PrescriptionEntity create(Map<String, Object> data) {
    var entity = new PrescriptionEntity();
    update(entity, data);
    return entity;
  }

  public void update(PrescriptionEntity entity, Map<String, Object> data) {
    if (data.containsKey("patientId")) entity.setPatientId(Values.uuid(data.get("patientId")));
    if (data.containsKey("doctorId")) entity.setDoctorId(Values.uuid(data.get("doctorId")));
    if (data.containsKey("medicine")) entity.setMedicine(Values.text(data.get("medicine")));
    if (data.get("quantity") != null) entity.setQuantity(Values.integer(data.get("quantity")));
    if (data.containsKey("validUntil")) entity.setValidUntil(Values.date(data.get("validUntil")));
    if (data.containsKey("instructions"))
      entity.setInstructions(Values.text(data.get("instructions")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
  }

  public Map<String, Object> response(PrescriptionEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getPatientId() != null) out.put("patientId", entity.getPatientId().toString());
    if (entity.getDoctorId() != null) out.put("doctorId", entity.getDoctorId().toString());
    if (entity.getMedicine() != null) out.put("medicine", entity.getMedicine());
    if (entity.getQuantity() != null) out.put("quantity", entity.getQuantity());
    if (entity.getValidUntil() != null) out.put("validUntil", entity.getValidUntil().toString());
    if (entity.getInstructions() != null) out.put("instructions", entity.getInstructions());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    return out;
  }
}
