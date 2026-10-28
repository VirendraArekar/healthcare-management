package com.hospital.patient.mapper;

import com.hospital.common.mapper.*;
import com.hospital.patient.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper implements AggregateMapper<PatientEntity> {
  public PatientEntity create(Map<String, Object> data) {
    var entity = new PatientEntity();
    update(entity, data);
    return entity;
  }

  public void update(PatientEntity entity, Map<String, Object> data) {
    if (data.containsKey("patientNumber"))
      entity.setPatientNumber(Values.text(data.get("patientNumber")));
    if (data.containsKey("firstName")) entity.setFirstName(Values.text(data.get("firstName")));
    if (data.containsKey("lastName")) entity.setLastName(Values.text(data.get("lastName")));
    if (data.containsKey("dateOfBirth"))
      entity.setDateOfBirth(Values.date(data.get("dateOfBirth")));
    if (data.containsKey("email")) entity.setEmail(Values.text(data.get("email")));
    if (data.containsKey("phone")) entity.setPhone(Values.text(data.get("phone")));
    if (data.containsKey("emergencyContact"))
      entity.setEmergencyContact(Values.text(data.get("emergencyContact")));
    if (data.containsKey("allergies")) entity.setAllergies(Values.text(data.get("allergies")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
  }

  public Map<String, Object> response(PatientEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getPatientNumber() != null) out.put("patientNumber", entity.getPatientNumber());
    if (entity.getFirstName() != null) out.put("firstName", entity.getFirstName());
    if (entity.getLastName() != null) out.put("lastName", entity.getLastName());
    if (entity.getDateOfBirth() != null) out.put("dateOfBirth", entity.getDateOfBirth().toString());
    if (entity.getEmail() != null) out.put("email", entity.getEmail());
    if (entity.getPhone() != null) out.put("phone", entity.getPhone());
    if (entity.getEmergencyContact() != null)
      out.put("emergencyContact", entity.getEmergencyContact());
    if (entity.getAllergies() != null) out.put("allergies", entity.getAllergies());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    return out;
  }
}
