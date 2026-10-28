package com.hospital.staff.mapper;

import com.hospital.common.mapper.*;
import com.hospital.staff.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class StaffMemberMapper implements AggregateMapper<StaffMemberEntity> {
  public StaffMemberEntity create(Map<String, Object> data) {
    var entity = new StaffMemberEntity();
    update(entity, data);
    return entity;
  }

  public void update(StaffMemberEntity entity, Map<String, Object> data) {
    if (data.containsKey("name")) entity.setName(Values.text(data.get("name")));
    if (data.containsKey("department")) entity.setDepartment(Values.text(data.get("department")));
    if (data.containsKey("specialty")) entity.setSpecialty(Values.text(data.get("specialty")));
    if (data.containsKey("email")) entity.setEmail(Values.text(data.get("email")));
    if (data.containsKey("phone")) entity.setPhone(Values.text(data.get("phone")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
  }

  public Map<String, Object> response(StaffMemberEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getName() != null) out.put("name", entity.getName());
    if (entity.getDepartment() != null) out.put("department", entity.getDepartment());
    if (entity.getSpecialty() != null) out.put("specialty", entity.getSpecialty());
    if (entity.getEmail() != null) out.put("email", entity.getEmail());
    if (entity.getPhone() != null) out.put("phone", entity.getPhone());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    return out;
  }
}
