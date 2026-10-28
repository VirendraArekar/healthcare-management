package com.hospital.pharmacy.dao;

import com.hospital.common.repository.RecordRepository;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.JsonCodec;
import com.hospital.pharmacy.repository.PharmacyRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PharmacyDao extends RecordRepository implements PharmacyRepository {
  @org.springframework.beans.factory.annotation.Autowired
  private com.hospital.pharmacy.repository.jpa.MedicineBatchJpaRepository batches;

  public PharmacyDao(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    super(db, codec, jpa);
  }

  public java.util.List<java.util.Map<String, Object>> findBatches() {
    Object[] args = new Object[] {};
    return db.queryForList("select * from batches order by expires_on limit 100", args);
  }

  public int insertBatch(
      java.util.UUID id, String medicine, int quantity, java.time.LocalDate expiry) {
    Object[] args = new Object[] {id, medicine, quantity, expiry};
    return db.update("insert into batches(id,medicine,quantity,expires_on) values(?,?,?,?)", args);
  }

  public int decreaseStock(int quantity, java.util.UUID id, int minimum) {
    Object[] args = new Object[] {quantity, id, minimum};
    return db.update(
        "update batches set quantity=quantity-?,version=version+1 where id=? and quantity>=? and"
            + " expires_on>current_date",
        args);
  }

  public int insertMovement(
      java.util.UUID id,
      java.util.UUID batch,
      java.util.UUID prescription,
      int quantity,
      String actor) {
    Object[] args = new Object[] {id, batch, prescription, quantity, actor};
    return db.update(
        "insert into movements(id,batch_id,prescription_id,quantity,actor) values(?,?,?,?,?)",
        args);
  }

  public Integer remainingStock(java.util.UUID id) {
    Object[] args = new Object[] {id};
    return db.queryForObject("select quantity from batches where id=?", Integer.class, args);
  }

  public void initializePrescription(java.util.UUID id) {
    db.update("insert into prescription_dispenses values(?,0) on conflict do nothing", id);
  }

  public int reservePrescription(java.util.UUID id, int qty, int maximum) {
    return db.update(
        "update prescription_dispenses set total_quantity=total_quantity+? where prescription_id=?"
            + " and total_quantity+?<=?",
        qty,
        id,
        qty,
        maximum);
  }

  public Object movements() {
    return java.util.Map.of(
        "content", db.queryForList("select * from movements order by created_at desc limit 100"));
  }

  public int adjust(java.util.UUID id, int delta) {
    return db.update(
        "update batches set quantity=quantity+?,version=version+1 where id=? and quantity+?>=0",
        delta,
        id,
        delta);
  }

  public Object alerts() {
    return java.util.Map.of(
        "content",
        db.queryForList(
            "select b.*,m.reorder_level from batches b join medicines m on m.id=b.medicine_id where"
                + " b.quantity<m.reorder_level or b.expires_on<=current_date+30 order by"
                + " b.expires_on"));
  }

  public java.util.List<java.util.Map<String, Object>> findBatch(java.util.UUID id) {
    return db.queryForList("select * from batches where id=?", id);
  }

  public Object batch(java.util.UUID id) {
    var batch =
        batches
            .findById(id)
            .orElseThrow(
                () ->
                    new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Batch not found"));
    return java.util.Map.of(
        "id",
        batch.getId(),
        "medicine",
        batch.getMedicine(),
        "quantity",
        batch.getQuantity(),
        "expiresOn",
        batch.getExpiresOn(),
        "batchNumber",
        batch.getBatchNumber(),
        "version",
        batch.getVersion());
  }

  public int reorderLevel(java.util.UUID batch) {
    return db.queryForObject(
        "select m.reorder_level from batches b join medicines m on m.id=b.medicine_id where b.id=?",
        Integer.class,
        batch);
  }

  public boolean claimAlert(java.util.UUID batch, String type) {
    return db.update(
            "insert into inventory_alerts(batch_id,alert_type) values(?,?) on conflict do nothing",
            batch,
            type)
        == 1;
  }

  public java.util.List<java.util.Map<String, Object>> expiring(int days) {
    return db.queryForList(
        "select id,medicine,expires_on,quantity from batches where expires_on<=current_date+? and"
            + " quantity>0 order by expires_on",
        days);
  }
}
