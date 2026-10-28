package com.hospital.common.repository;

import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OutboxRepository {
  private final JdbcTemplate db;

  public OutboxRepository(JdbcTemplate db) {
    this.db = db;
  }

  public List<Map<String, Object>> pending() {
    return db.queryForList(
        "select * from outbox where sent_at is null order by created_at limit 50 for update skip"
            + " locked");
  }

  public void sent(Object id) {
    db.update("update outbox set sent_at=now() where id=?", id);
  }
}
