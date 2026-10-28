package com.hospital.billing.dao;

import com.hospital.billing.repository.BillingRepository;
import com.hospital.common.repository.RecordRepository;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.JsonCodec;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BillingDao extends RecordRepository implements BillingRepository {
  public BillingDao(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    super(db, codec, jpa);
  }

  public java.util.List<java.util.Map<String, Object>> lockInvoice(java.util.UUID id) {
    Object[] args = new Object[] {id};
    return db.queryForList("select * from invoices where id=? for update", args);
  }

  public java.util.List<java.util.Map<String, Object>> findPaymentByIdempotencyKey(
      java.util.UUID key) {
    Object[] args = new Object[] {key};
    return db.queryForList("select * from payments where idempotency_key=?", args);
  }

  public int insertPayment(
      java.util.UUID id, java.util.UUID invoice, java.util.UUID key, java.math.BigDecimal amount) {
    Object[] args = new Object[] {id, invoice, key, amount};
    return db.update(
        "insert into payments(id,invoice_id,idempotency_key,amount) values(?,?,?,?)", args);
  }

  public int markInvoicePaid(java.util.UUID id) {
    Object[] args = new Object[] {id};
    return db.update("update invoices set status='PAID',version=version+1 where id=?", args);
  }

  public long pendingRefunds(java.util.UUID id) {
    return db.queryForObject(
        "select count(*) from refunds where invoice_id::text=? and status"
            + " IN('REQUESTED','APPROVED','COMPLETED')",
        Long.class,
        id.toString());
  }

  public Object payments() {
    return java.util.Map.of(
        "content", db.queryForList("select * from payments order by created_at desc limit 100"));
  }

  public Object balances() {
    return listByStatus("invoice", 0, 100, "", "UNPAID");
  }

  public void refundInvoice(java.util.UUID id) {
    db.update("update invoices set status='REFUNDED',version=version+1 where id=?", id);
  }

  public Object findPatientRecords(String owner, int page) {
    return list("invoice", page, 20, "", owner);
  }

  @Override
  public java.util.Map<String, Object> dto(java.util.Map<String, Object> row) {
    if (row.containsKey("data")) return super.dto(row);
    return get("invoice", java.util.UUID.fromString(row.get("id").toString()));
  }

  public void lockRefund(java.util.UUID id) {
    db.queryForList("select id from refunds where id=? for update", id);
  }

  public java.util.List<java.util.Map<String, Object>> refundSettlement(java.util.UUID id) {
    return db.queryForList("select * from refund_settlements where refund_id=?", id);
  }

  public void insertRefundSettlement(
      java.util.UUID settlement,
      java.util.UUID refund,
      java.util.UUID invoice,
      java.math.BigDecimal amount) {
    db.update(
        "insert into refund_settlements(id,refund_id,invoice_id,amount) values(?,?,?,?)",
        settlement,
        refund,
        invoice,
        amount);
  }
}
