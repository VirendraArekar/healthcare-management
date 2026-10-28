package com.hospital.medicalrecord.mapper;

import com.hospital.common.mapper.*;
import com.hospital.medicalrecord.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class EncounterMapper implements AggregateMapper<EncounterEntity> {
  public EncounterEntity create(Map<String, Object> data) {
    var entity = new EncounterEntity();
    update(entity, data);
    return entity;
  }

  public void update(EncounterEntity entity, Map<String, Object> data) {
    if (data.containsKey("patientId")) entity.setPatientId(Values.uuid(data.get("patientId")));
    if (data.containsKey("doctorId")) entity.setDoctorId(Values.uuid(data.get("doctorId")));
    if (data.containsKey("diagnosis")) entity.setDiagnosis(Values.text(data.get("diagnosis")));
    if (data.containsKey("notes")) entity.setNotes(Values.text(data.get("notes")));
    if (data.containsKey("treatment")) entity.setTreatment(Values.text(data.get("treatment")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
  }

  public Map<String, Object> response(EncounterEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getPatientId() != null) out.put("patientId", entity.getPatientId().toString());
    if (entity.getDoctorId() != null) out.put("doctorId", entity.getDoctorId().toString());
    if (entity.getDiagnosis() != null) out.put("diagnosis", entity.getDiagnosis());
    if (entity.getNotes() != null) out.put("notes", entity.getNotes());
    if (entity.getTreatment() != null) out.put("treatment", entity.getTreatment());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    return out;
  }
}
