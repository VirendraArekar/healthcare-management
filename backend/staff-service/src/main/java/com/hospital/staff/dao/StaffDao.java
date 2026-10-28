package com.hospital.staff.dao;

import com.hospital.common.repository.RecordRepository;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.JsonCodec;
import com.hospital.staff.repository.StaffRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StaffDao extends RecordRepository implements StaffRepository {
  public StaffDao(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    super(db, codec, jpa);
  }

  public boolean available(
      java.util.UUID staff,
      java.time.LocalDate day,
      java.time.LocalTime start,
      java.time.LocalTime end) {
    long working =
        db.queryForObject(
            "select count(*) from working_schedules where staff_id=? and day_of_week=? and"
                + " status='ACTIVE' and starts_at<=? and ends_at>=?",
            Long.class,
            staff,
            day.getDayOfWeek().name(),
            java.sql.Time.valueOf(start),
            java.sql.Time.valueOf(end));
    long leave =
        db.queryForObject(
            "select count(*) from staff_leave where staff_id=? and status='APPROVED' and"
                + " starts_on<=? and ends_on>=?",
            Long.class,
            staff,
            day,
            day);
    return working > 0 && leave == 0;
  }
}
