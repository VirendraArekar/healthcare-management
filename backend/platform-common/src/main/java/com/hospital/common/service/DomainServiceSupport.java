package com.hospital.common.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.repository.ResourceOperations;
import com.hospital.common.util.JsonCodec;
import java.util.*;

public abstract class DomainServiceSupport {
  @org.springframework.beans.factory.annotation.Autowired
  protected com.hospital.common.client.DirectoryClient directory;

  @org.springframework.beans.factory.annotation.Autowired protected ReferenceCache referenceCache;
  protected final ResourceOperations records;
  protected final EventPublisher events;
  protected final AuditService audit;
  protected final JsonCodec codec;

  protected DomainServiceSupport(
      ResourceOperations records, EventPublisher events, AuditService audit, JsonCodec codec) {
    this.records = records;
    this.events = events;
    this.audit = audit;
    this.codec = codec;
  }

  protected Map<String, Object> create(String kind, Map<String, Object> data, String event) {
    var record = records.create(kind, data);
    publish(event, record);
    return record;
  }

  protected Map<String, Object> update(
      String kind, UUID id, Map<String, Object> data, long version, String event) {
    var record = records.update(kind, id, data, version);
    publish(event, record);
    return record;
  }

  private void publish(String event, Map<String, Object> record) {
    if (event == null) return;
    var payload = new LinkedHashMap<String, Object>();
    payload.put("recordId", record.get("id").toString());
    for (String key :
        List.of(
            "patientId",
            "doctorId",
            "status",
            "total",
            "email",
            "startsAt",
            "department",
            "expiresOn",
            "quantity")) {
      if (record.get(key) != null) payload.put(key, record.get(key));
    }
    if (event.startsWith("patient.")) payload.put("patientId", record.get("id").toString());
    if (event.startsWith("staff.")) payload.put("staffId", record.get("id").toString());
    events.publish(event, UUID.fromString(record.get("id").toString()), payload);
  }
}
