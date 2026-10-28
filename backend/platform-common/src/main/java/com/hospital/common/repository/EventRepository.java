package com.hospital.common.repository;

import com.hospital.common.event.DomainEvent;
import com.hospital.common.util.JsonCodec;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepository {
  private final JdbcTemplate db;
  private final JsonCodec codec;

  public EventRepository(JdbcTemplate db, JsonCodec codec) {
    this.db = db;
    this.codec = codec;
  }

  public void save(DomainEvent event) {
    db.update(
        "insert into outbox(id,topic,aggregate_id,payload) values(?,?,?,?::jsonb)",
        event.eventId(),
        event.eventType(),
        event.aggregateId(),
        codec.json(event));
  }
}
