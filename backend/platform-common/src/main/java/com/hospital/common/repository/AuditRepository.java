package com.hospital.common.repository;

import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditRepository {
  private final JdbcTemplate db;

  public AuditRepository(JdbcTemplate db) {
    this.db = db;
  }

  public void save(String actor, String action, UUID id) {
    db.update("insert into audit_log(actor,action,aggregate_id) values(?,?,?)", actor, action, id);
  }

  public Object page(int page, int size) {
    return Map.of(
        "content",
        db.queryForList(
            "select * from audit_log order by occurred_at desc limit ? offset ?",
            size,
            (long) page * size),
        "page",
        page,
        "size",
        size);
  }
}
