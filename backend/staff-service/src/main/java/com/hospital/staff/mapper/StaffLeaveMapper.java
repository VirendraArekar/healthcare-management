package com.hospital.staff.mapper;

import com.hospital.common.mapper.*;
import com.hospital.staff.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class StaffLeaveMapper implements AggregateMapper<StaffLeaveEntity> {
  public StaffLeaveEntity create(Map<String, Object> data) {
    var entity = new StaffLeaveEntity();
    update(entity, data);
    return entity;
  }

  public void update(StaffLeaveEntity entity, Map<String, Object> data) {
    if (data.containsKey("staffId")) entity.setStaffId(Values.uuid(data.get("staffId")));
    if (data.containsKey("startsOn")) entity.setStartsOn(Values.date(data.get("startsOn")));
    if (data.containsKey("endsOn")) entity.setEndsOn(Values.date(data.get("endsOn")));
    if (data.containsKey("reason")) entity.setReason(Values.text(data.get("reason")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
    if (data.containsKey("requestedBy"))
      entity.setRequestedBy(Values.uuid(data.get("requestedBy")));
    if (data.containsKey("approvedBy")) entity.setApprovedBy(Values.uuid(data.get("approvedBy")));
  }

  public Map<String, Object> response(StaffLeaveEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getStaffId() != null) out.put("staffId", entity.getStaffId().toString());
    if (entity.getStartsOn() != null) out.put("startsOn", entity.getStartsOn().toString());
    if (entity.getEndsOn() != null) out.put("endsOn", entity.getEndsOn().toString());
    if (entity.getReason() != null) out.put("reason", entity.getReason());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    if (entity.getRequestedBy() != null) out.put("requestedBy", entity.getRequestedBy().toString());
    if (entity.getApprovedBy() != null) out.put("approvedBy", entity.getApprovedBy().toString());
    return out;
  }
}
