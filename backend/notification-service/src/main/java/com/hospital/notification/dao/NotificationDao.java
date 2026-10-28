package com.hospital.notification.dao;

import com.hospital.common.repository.RecordRepository;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.JsonCodec;
import com.hospital.notification.repository.NotificationRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDao extends RecordRepository implements NotificationRepository {
  @org.springframework.beans.factory.annotation.Autowired
  private com.hospital.notification.dao.NotificationQueryDao queries;

  public NotificationDao(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    super(db, codec, jpa);
  }

  public java.util.List<java.util.Map<String, Object>> lockPendingEmails(Object... args) {
    return db.queryForList(
        "select * from email_jobs where sent_at is null and attempts<5 order by created_at"
            + " limit 20 for update skip locked",
        args);
  }

  public int markEmailSent(Object... args) {
    return db.update("update email_jobs set sent_at=now() where id=?", args);
  }

  public int incrementEmailAttempts(Object... args) {
    return db.update("update email_jobs set attempts=attempts+1 where id=?", args);
  }

  public int claimEvent(Object... args) {
    return db.update(
        "insert into consumed_events(event_id) values(?) on conflict do nothing", args);
  }

  public int enqueueEmail(Object... args) {
    return db.update("insert into email_jobs(id,subject,body) values(?,?,?)", args);
  }

  public Object failedEmails() {
    return java.util.Map.of(
        "content",
        db.queryForList(
            "select id,subject,attempts,created_at from email_jobs where attempts>=5 and sent_at is"
                + " null order by created_at"));
  }

  public void retryEmail(java.util.UUID id) {
    db.update("update email_jobs set attempts=0 where id=? and sent_at is null", id);
  }

  public void contact(java.util.UUID id, String email) {
    db.update(
        "insert into patient_contacts values(?,?) on conflict(patient_id) do update set"
            + " email=excluded.email",
        id,
        email);
  }

  public void routeEmail(java.util.UUID event, java.util.UUID patient) {
    db.update(
        "update email_jobs set recipient=(select email from patient_contacts where patient_id=?)"
            + " where id=?",
        patient,
        event);
  }

  public java.util.Map<String, Object> audience(
      int page,
      int size,
      java.util.Set<String> prefixes,
      java.util.UUID patient,
      java.util.Set<java.util.UUID> assigned,
      java.util.UUID actor) {
    var response = queries.list(page, size, prefixes, patient, assigned);
    var rows = (java.util.List<java.util.Map<String, Object>>) response.get("content");
    var read =
        new java.util.HashSet<java.util.UUID>(
            db.queryForList(
                "select notification_id from notification_reads where actor_id=?",
                java.util.UUID.class,
                actor));
    for (var row : rows)
      row.put("read", read.contains(java.util.UUID.fromString(row.get("id").toString())));
    return response;
  }

  public void markRead(java.util.UUID notification, java.util.UUID actor) {
    db.update(
        "insert into notification_reads(notification_id,actor_id) values(?,?) on conflict do"
            + " nothing",
        notification,
        actor);
  }
}
