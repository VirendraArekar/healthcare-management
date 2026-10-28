package com.hospital.medicalrecord.dao;

import com.hospital.common.repository.RecordRepository;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.JsonCodec;
import com.hospital.medicalrecord.repository.MedicalRecordRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MedicalRecordDao extends RecordRepository implements MedicalRecordRepository {
  public MedicalRecordDao(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    super(db, codec, jpa);
  }

  public int insertRecordHistory(
      java.util.UUID id, java.util.UUID record, Object version, String data, String actor) {
    Object[] args = new Object[] {id, record, version, data, actor};
    return db.update(
        "insert into record_history(id,record_id,version,data,actor) values(?,?,?,?,?)", args);
  }

  public Object history(java.util.UUID id) {
    return java.util.Map.of(
        "content",
        db.queryForList(
            "select version,actor,created_at,data from record_history where record_id=? order by"
                + " version",
            id));
  }

  public Object observations(java.util.UUID id) {
    return java.util.Map.of(
        "content",
        db
            .queryForList("select id from vital_signs where encounter_id=? order by created_at", id)
            .stream()
            .map(row -> get("vitals", (java.util.UUID) row.get("id")))
            .toList());
  }
}
