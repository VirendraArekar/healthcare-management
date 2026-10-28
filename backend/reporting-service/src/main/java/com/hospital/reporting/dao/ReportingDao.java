package com.hospital.reporting.dao;

import com.hospital.common.repository.RecordRepository;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.JsonCodec;
import com.hospital.reporting.repository.ReportingRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReportingDao extends RecordRepository implements ReportingRepository {
  @org.springframework.beans.factory.annotation.Autowired
  private com.hospital.reporting.repository.jpa.ReportCountJpaRepository counts;

  public ReportingDao(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    super(db, codec, jpa);
  }

  public int claimEvent(Object... args) {
    return db.update(
        "insert into consumed_events(event_id) values(?) on conflict do nothing", args);
  }

  public int projectEvent(Object... args) {
    return db.update(
        "insert into report_counts(event_type,total) values(?,1) on conflict(event_type) do update"
            + " set total=report_counts.total+1",
        args);
  }

  public java.util.List<java.util.Map<String, Object>> findReportCounts(Object... args) {
    return counts.findAll(org.springframework.data.domain.Sort.by("eventType")).stream()
        .map(
            row ->
                java.util.Map.<String, Object>of(
                    "event_type", row.getEventType(), "total", row.getTotal()))
        .toList();
  }

  public java.util.List<java.util.Map<String, Object>> findCsvReportCounts(Object... args) {
    return db.queryForList("select * from report_counts order by event_type", args);
  }

  public java.util.List<java.util.Map<String, Object>> findPdfReportCounts(Object... args) {
    return db.queryForList("select * from report_counts order by event_type", args);
  }

  public void daily(java.time.LocalDate day, String type, java.math.BigDecimal amount) {
    db.update(
        "insert into daily_metrics(day,event_type,total,amount) values(?,?,1,?) on"
            + " conflict(day,event_type) do update set"
            + " total=daily_metrics.total+1,amount=daily_metrics.amount+excluded.amount",
        day,
        type,
        amount);
  }

  public void doctor(java.util.UUID id) {
    db.update(
        "insert into doctor_utilization values(?,1) on conflict(doctor_id) do update set"
            + " appointments=doctor_utilization.appointments+1",
        id);
  }

  public Object daily() {
    return java.util.Map.of(
        "content",
        db.queryForList("select * from daily_metrics order by day desc,event_type limit 366"));
  }

  public Object monthly() {
    return java.util.Map.of(
        "content",
        db.queryForList(
            "select date_trunc('month',day) as month,event_type,sum(total) as total,sum(amount) as"
                + " amount from daily_metrics group by 1,2 order by 1 desc,2 limit 240"));
  }

  public Object utilization() {
    return java.util.Map.of(
        "content",
        db.queryForList("select * from doctor_utilization order by appointments desc limit 100"));
  }
}
