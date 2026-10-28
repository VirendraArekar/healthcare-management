package com.hospital.patient.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.patient.repository.PatientRepository;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientService extends DomainServiceSupport {
  private final PatientRepository repository;

  public PatientService(
      PatientRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PATIENT')")
  public Object list(int page, int size, String q, Jwt jwt) {
    if (!owner(jwt).isEmpty())
      return Map.of("content", List.of(repository.get("patient", UUID.fromString(owner(jwt)))));
    if (com.hospital.common.security.Actor.has(jwt, "DOCTOR")
        || com.hospital.common.security.Actor.has(jwt, "NURSE"))
      return repository.listForPatients(
          "patient",
          page,
          size,
          q,
          repository.assignedPatientIds(UUID.fromString(jwt.getSubject())));
    return repository.list("patient", page, size, q, "");
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PATIENT')")
  public Object get(UUID id, Jwt jwt) {
    access(id, jwt);
    var r = repository.get("patient", id);
    if (!owner(jwt).isEmpty()
        && !owner(jwt).equals(Objects.toString(r.get("patientId"), r.get("id").toString())))
      throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
    audit.record(jwt.getSubject(), "READ_patient", id);
    return r;
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  @Transactional
  public Object create(Map<String, Object> body, Jwt jwt) {
    Validation.required(body, "firstName");
    Validation.required(body, "lastName");
    Validation.required(body, "dateOfBirth");
    Validation.required(body, "email");
    Validation.require(
        Validation.required(body, "email").matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"),
        "Invalid email");
    Validation.require(
        !LocalDate.parse(body.get("dateOfBirth").toString()).isAfter(LocalDate.now()),
        "Birth date must be in the past");
    body.put("patientNumber", "PAT-" + UUID.randomUUID());
    body.put("status", "ACTIVE");
    var r = create("patient", body, "patient.registered.v1");
    audit.record(jwt.getSubject(), "CREATE_patient", UUID.fromString(r.get("id").toString()));
    return r;
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  @Transactional
  public Object update(UUID id, Map<String, Object> body, Jwt jwt) {
    long version = Long.parseLong(Validation.required(body, "version"));
    var current = repository.get("patient", id);
    body.put("patientNumber", current.get("patientNumber"));
    Validation.required(body, "firstName");
    Validation.required(body, "lastName");
    audit.record(jwt.getSubject(), "UPDATE_PATIENT", id);
    return update("patient", id, body, version, "patient.updated.v1");
  }

  private String owner(Jwt jwt) {
    return jwt.getClaimAsStringList("roles").contains("PATIENT")
        ? Objects.requireNonNullElse(jwt.getClaimAsString("patientId"), "unbound")
        : "";
  }

  public Object exists(UUID id) {
    var r = repository.get("patient", id);
    Validation.require("ACTIVE".equals(r.get("status")), "Patient is not active");
    return Map.of("id", id, "status", "ACTIVE");
  }

  public Object access(UUID id, Jwt jwt) {
    repository.get("patient", id);
    if (com.hospital.common.security.Actor.has(jwt, "PATIENT")) {
      if (!com.hospital.common.security.Actor.patient(jwt).equals(id))
        throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
    } else if (!com.hospital.common.security.Actor.has(jwt, "ADMIN")
        && !com.hospital.common.security.Actor.has(jwt, "RECEPTIONIST")
        && !repository.assigned(id, UUID.fromString(jwt.getSubject())))
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.FORBIDDEN, "Patient is not assigned to this clinician");
    return Map.of("id", id, "authorized", true);
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object assign(UUID id, Map<String, Object> b, Jwt jwt) {
    repository.get("patient", id);
    String role = Validation.required(b, "role");
    Validation.require(Set.of("DOCTOR", "NURSE").contains(role), "Invalid care role");
    UUID clinician = UUID.fromString(Validation.required(b, "clinicianId"));
    directory.clinician(clinician, role);
    repository.assign(id, clinician, role);
    audit.record(jwt.getSubject(), "ASSIGN_CARE", id);
    return Map.of("status", "ASSIGNED");
  }

  @PreAuthorize("hasRole('PATIENT')")
  public Object profile(Map<String, Object> b, Jwt jwt) {
    UUID id = com.hospital.common.security.Actor.patient(jwt);
    var r = repository.get("patient", id);
    long version = ((Number) r.remove("version")).longValue();
    for (String key : List.of("phone", "email", "emergencyContact")) {
      if (b.get(key) != null) r.put(key, b.get(key));
    }
    return update("patient", id, r, version, "patient.updated.v1");
  }

  @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
  public Object assignedIds(Jwt jwt) {
    return Map.of("ids", repository.assignedPatientIds(UUID.fromString(jwt.getSubject())));
  }

  public Object references(int page, String q, Jwt jwt) {
    Map<String, Object> result;
    if (com.hospital.common.security.Actor.has(jwt, "PATIENT"))
      result =
          repository.list(
              "patient", page, 20, q, com.hospital.common.security.Actor.patient(jwt).toString());
    else if (com.hospital.common.security.Actor.has(jwt, "DOCTOR")
        || com.hospital.common.security.Actor.has(jwt, "NURSE"))
      result =
          repository.listForPatients(
              "patient",
              page,
              100,
              q,
              repository.assignedPatientIds(UUID.fromString(jwt.getSubject())));
    else result = repository.list("patient", page, 100, q, "");
    var content =
        ((List<Map<String, Object>>) result.get("content"))
            .stream()
                .map(
                    row ->
                        Map.of(
                            "id",
                            row.get("id"),
                            "patientNumber",
                            row.get("patientNumber"),
                            "name",
                            row.get("firstName") + " " + row.get("lastName")))
                .toList();
    return Map.of("content", content, "totalElements", result.get("totalElements"));
  }
}
