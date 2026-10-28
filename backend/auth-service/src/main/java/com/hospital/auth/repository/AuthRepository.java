package com.hospital.auth.repository;

import com.hospital.common.repository.ResourceOperations;

public interface AuthRepository extends ResourceOperations {
  java.util.List<java.util.Map<String, Object>> findEnabledUserByEmail(String email);

  java.util.List<java.util.Map<String, Object>> lockRefreshToken(String hash);

  int revokeRefreshToken(Object id);

  int revokeUserToken(String hash, java.util.UUID user);

  java.util.List<com.hospital.auth.dto.UserAccountResponse> findUsers();

  int insertUser(
      java.util.UUID id,
      String email,
      String password,
      String role,
      java.util.UUID patient,
      java.util.UUID staff);

  int insertRefreshToken(java.util.UUID id, Object user, String hash, java.sql.Timestamp expiry);

  int insertDemoUser(java.util.UUID id, String email, String hash, String role);

  void failedLogin(Object user);

  void clearFailures(Object user);

  void revokeFamily(Object family);

  void setFamily(Object hash, Object family);

  void resetToken(String hash, Object user);

  java.util.List<java.util.Map<String, Object>> lockResetToken(String hash);

  void setPassword(Object user, String hash);

  boolean updateAccount(
      java.util.UUID id,
      String role,
      boolean enabled,
      long version,
      java.util.UUID staff,
      java.util.UUID patient);

  java.util.List<java.util.Map<String, Object>> accountReference(java.util.UUID id);

  java.util.List<java.util.Map<String, Object>> clinicians();
}
