package com.hospital.notification.mapper;

import com.hospital.common.mapper.*;
import com.hospital.notification.entity.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper implements AggregateMapper<NotificationEntity> {
  public NotificationEntity create(Map<String, Object> data) {
    var entity = new NotificationEntity();
    update(entity, data);
    return entity;
  }

  public void update(NotificationEntity entity, Map<String, Object> data) {
    if (data.containsKey("patientId")) entity.setPatientId(Values.uuid(data.get("patientId")));
    if (data.containsKey("eventType")) entity.setEventType(Values.text(data.get("eventType")));
    if (data.containsKey("aggregateId"))
      entity.setAggregateId(Values.uuid(data.get("aggregateId")));
    if (data.containsKey("message")) entity.setMessage(Values.text(data.get("message")));
    if (data.get("read") != null) entity.setRead(Values.bool(data.get("read")));
  }

  public Map<String, Object> response(NotificationEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getPatientId() != null) out.put("patientId", entity.getPatientId().toString());
    if (entity.getEventType() != null) out.put("eventType", entity.getEventType());
    if (entity.getAggregateId() != null) out.put("aggregateId", entity.getAggregateId().toString());
    if (entity.getMessage() != null) out.put("message", entity.getMessage());
    if (entity.getRead() != null) out.put("read", entity.getRead());
    return out;
  }
}
