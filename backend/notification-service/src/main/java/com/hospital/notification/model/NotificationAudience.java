package com.hospital.notification.model;

import com.hospital.common.security.Actor;
import java.util.*;
import org.springframework.security.oauth2.jwt.Jwt;

public record NotificationAudience(Set<String> prefixes, UUID patientId, boolean assignedOnly) {
  public static NotificationAudience forActor(Jwt actor) {
    if (Actor.has(actor, "ADMIN")) return new NotificationAudience(Set.of(), null, false);
    if (Actor.has(actor, "PATIENT"))
      return new NotificationAudience(Set.of(), Actor.patient(actor), false);
    if (Actor.has(actor, "DOCTOR") || Actor.has(actor, "NURSE"))
      return new NotificationAudience(
          Set.of("patient.", "appointment.", "lab.", "medical-record.", "notification."),
          null,
          true);
    if (Actor.has(actor, "RECEPTIONIST"))
      return new NotificationAudience(
          Set.of("patient.", "appointment.", "notification."), null, false);
    if (Actor.has(actor, "LAB_TECHNICIAN"))
      return new NotificationAudience(Set.of("lab."), null, false);
    if (Actor.has(actor, "PHARMACIST"))
      return new NotificationAudience(Set.of("pharmacy."), null, false);
    if (Actor.has(actor, "BILLING_STAFF"))
      return new NotificationAudience(Set.of("billing."), null, false);
    throw new org.springframework.web.server.ResponseStatusException(
        org.springframework.http.HttpStatus.FORBIDDEN);
  }
}
