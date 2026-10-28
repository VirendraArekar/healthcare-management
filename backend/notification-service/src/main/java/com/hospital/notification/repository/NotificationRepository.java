package com.hospital.notification.repository;

import com.hospital.common.repository.ResourceOperations;

public interface NotificationRepository extends ResourceOperations {
  java.util.List<java.util.Map<String, Object>> lockPendingEmails(Object... args);

  int markEmailSent(Object... args);

  int incrementEmailAttempts(Object... args);

  int claimEvent(Object... args);

  int enqueueEmail(Object... args);

  Object failedEmails();

  void retryEmail(java.util.UUID id);

  void contact(java.util.UUID id, String email);

  void routeEmail(java.util.UUID event, java.util.UUID patient);

  java.util.Map<String, Object> audience(
      int page,
      int size,
      java.util.Set<String> prefixes,
      java.util.UUID patient,
      java.util.Set<java.util.UUID> assigned,
      java.util.UUID actor);

  void markRead(java.util.UUID notification, java.util.UUID actor);
}
