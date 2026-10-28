package com.hospital.staff.mapper;

import com.hospital.common.mapper.*;
import com.hospital.staff.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class WorkingScheduleMapper implements AggregateMapper<WorkingScheduleEntity> {
  public WorkingScheduleEntity create(Map<String, Object> data) {
    var entity = new WorkingScheduleEntity();
    update(entity, data);
    return entity;
  }

  public void update(WorkingScheduleEntity entity, Map<String, Object> data) {
    if (data.containsKey("staffId")) entity.setStaffId(Values.uuid(data.get("staffId")));
    if (data.containsKey("dayOfWeek")) entity.setDayOfWeek(Values.text(data.get("dayOfWeek")));
    if (data.containsKey("startsAt")) entity.setStartsAt(Values.time(data.get("startsAt")));
    if (data.containsKey("endsAt")) entity.setEndsAt(Values.time(data.get("endsAt")));
    if (data.get("timezone") != null) entity.setTimezone(Values.text(data.get("timezone")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
  }

  public Map<String, Object> response(WorkingScheduleEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getStaffId() != null) out.put("staffId", entity.getStaffId().toString());
    if (entity.getDayOfWeek() != null) out.put("dayOfWeek", entity.getDayOfWeek());
    if (entity.getStartsAt() != null) out.put("startsAt", entity.getStartsAt());
    if (entity.getEndsAt() != null) out.put("endsAt", entity.getEndsAt());
    if (entity.getTimezone() != null) out.put("timezone", entity.getTimezone());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    return out;
  }
}
