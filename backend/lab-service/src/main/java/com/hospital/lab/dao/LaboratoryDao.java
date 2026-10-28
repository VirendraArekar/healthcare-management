package com.hospital.lab.dao;

import com.hospital.common.repository.RecordRepository;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.JsonCodec;
import com.hospital.lab.repository.LaboratoryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LaboratoryDao extends RecordRepository implements LaboratoryRepository {
  public LaboratoryDao(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    super(db, codec, jpa);
  }

  public Object findPatientRecords(String owner, int page) {
    return listByStatus("order", page, 20, owner, "VERIFIED");
  }

  public void sample(java.util.UUID order, java.util.UUID actor) {
    db.update(
        "insert into laboratory_samples(id,order_id,barcode,collected_by) values(?,?,?,?)",
        java.util.UUID.randomUUID(),
        order,
        "LAB-" + order.toString(),
        actor);
  }

  public void transitionHistory(
      java.util.UUID order, String previous, String next, java.util.UUID actor) {
    db.update(
        "insert into laboratory_history(id,order_id,previous_status,next_status,actor_id)"
            + " values(?,?,?,?,?)",
        java.util.UUID.randomUUID(),
        order,
        previous,
        next,
        actor);
  }

  public Object orderHistory(java.util.UUID order) {
    return java.util.Map.of(
        "content",
        db.queryForList(
            "select * from laboratory_history where order_id=? order by occurred_at", order));
  }

  public java.util.List<java.util.Map<String, Object>> findTest(String name) {
    return db.queryForList(
        "select id,reference_range,unit from laboratory_tests where lower(name)=lower(?) and"
            + " status='ACTIVE'",
        name);
  }
}
