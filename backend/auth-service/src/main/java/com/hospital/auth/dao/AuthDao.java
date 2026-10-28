package com.hospital.auth.dao;

import com.hospital.auth.repository.AuthRepository;
import com.hospital.common.repository.RecordRepository;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.JsonCodec;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthDao extends RecordRepository implements AuthRepository {
  @org.springframework.beans.factory.annotation.Autowired
  private com.hospital.auth.repository.jpa.UserAccountJpaRepository users;

  @org.springframework.beans.factory.annotation.Autowired
  private com.hospital.auth.mapper.UserAccountMapper userMapper;

  public AuthDao(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    super(db, codec, jpa);
  }

  public java.util.List<java.util.Map<String, Object>> findEnabledUserByEmail(String email) {
    Object[] args = new Object[] {email};
    return db.queryForList("select * from users where email=? and enabled=true", args);
  }

  public java.util.List<java.util.Map<String, Object>> lockRefreshToken(String hash) {
    Object[] args = new Object[] {hash};
    return db.queryForList(
        "select u.*,r.id as refresh_id,r.revoked,r.expires_at,r.family_id from refresh_tokens r"
            + " join users u on u.id=r.user_id where r.token_hash=? and u.enabled=true for update"
            + " of r",
        args);
  }

  public int revokeRefreshToken(Object id) {
    Object[] args = new Object[] {id};
    return db.update("update refresh_tokens set revoked=true where id=?", args);
  }

  public int revokeUserToken(String hash, java.util.UUID user) {
    Object[] args = new Object[] {hash, user};
    return db.update(
        "update refresh_tokens set revoked=true where token_hash=? and user_id=?", args);
  }

  public java.util.List<com.hospital.auth.dto.UserAccountResponse> findUsers() {
    return users.findAll(org.springframework.data.domain.Sort.by("email")).stream()
        .map(userMapper::response)
        .toList();
  }

  public int insertUser(
      java.util.UUID id,
      String email,
      String password,
      String role,
      java.util.UUID patient,
      java.util.UUID staff) {
    Object[] args = new Object[] {id, email, password, role, patient, staff};
    return db.update(
        "insert into users(id,email,password_hash,role,patient_id,staff_id) values(?,?,?,?,?,?)",
        args);
  }

  public int insertRefreshToken(
      java.util.UUID id, Object user, String hash, java.sql.Timestamp expiry) {
    Object[] args = new Object[] {id, user, hash, expiry};
    return db.update(
        "insert into refresh_tokens(id,user_id,token_hash,expires_at) values(?,?,?,?)", args);
  }

  public int insertDemoUser(java.util.UUID id, String email, String hash, String role) {
    Object[] args = new Object[] {id, email, hash, role};
    return db.update(
        "insert into users(id,email,password_hash,role) values(?,?,?,?) on conflict(email) do"
            + " nothing",
        args);
  }

  public void failedLogin(Object user) {
    db.update(
        "update users set failed_attempts=failed_attempts+1,locked_until=case when"
            + " failed_attempts>=4 then now()+interval '15 minutes' else locked_until end where"
            + " id=?",
        user);
  }

  public void clearFailures(Object user) {
    db.update("update users set failed_attempts=0,locked_until=null where id=?", user);
  }

  public void revokeFamily(Object family) {
    db.update("update refresh_tokens set revoked=true where family_id=?", family);
  }

  public void setFamily(Object hash, Object family) {
    db.update("update refresh_tokens set family_id=? where token_hash=?", family, hash);
  }

  public void resetToken(String hash, Object user) {
    db.update(
        "insert into password_reset_tokens(token_hash,user_id,expires_at) values(?,?,now()+interval"
            + " '15 minutes')",
        hash,
        user);
  }

  public java.util.List<java.util.Map<String, Object>> lockResetToken(String hash) {
    return db.queryForList(
        "select * from password_reset_tokens where token_hash=? and used=false and expires_at>now()"
            + " for update",
        hash);
  }

  public void setPassword(Object user, String hash) {
    db.update(
        "update users set password_hash=?,failed_attempts=0,locked_until=null where id=?",
        hash,
        user);
    db.update("update refresh_tokens set revoked=true where user_id=?", user);
    db.update("update password_reset_tokens set used=true where user_id=?", user);
  }

  public boolean updateAccount(
      java.util.UUID id,
      String role,
      boolean enabled,
      long version,
      java.util.UUID staff,
      java.util.UUID patient) {
    int changed =
        db.update(
            "update users set role=?,enabled=?,staff_id=?,patient_id=?,version=version+1 where id=?"
                + " and version=?",
            role,
            enabled,
            staff,
            patient,
            id,
            version);
    if (changed == 1) db.update("update refresh_tokens set revoked=true where user_id=?", id);
    return changed == 1;
  }

  public java.util.List<java.util.Map<String, Object>> accountReference(java.util.UUID id) {
    return db.queryForList("select id,role,enabled,staff_id,patient_id from users where id=?", id);
  }

  public java.util.List<java.util.Map<String, Object>> clinicians() {
    return db.queryForList(
        "select id,email,role,staff_id from users where enabled=true and role IN('DOCTOR','NURSE')"
            + " order by email");
  }
}
