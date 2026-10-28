package com.hospital.common.client;

import com.hospital.common.security.Actor;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class DirectoryClient {
  private final RestClient http;

  @org.springframework.beans.factory.annotation.Autowired
  private org.springframework.core.env.Environment environment;

  @org.springframework.beans.factory.annotation.Value("${AUTH_URL:http://auth-service:8080}")
  private String auth;

  private final String patient, staff, medical;

  public DirectoryClient(
      @Value("${PATIENT_URL:http://patient-service:8080}") String patient,
      @Value("${STAFF_URL:http://staff-service:8080}") String staff,
      @Value("${MEDICAL_RECORD_URL:http://medical-record-service:8080}") String medical) {
    this.patient = patient;
    this.staff = staff;
    this.medical = medical;
    var f = new SimpleClientHttpRequestFactory();
    f.setConnectTimeout(2000);
    f.setReadTimeout(3000);
    http = RestClient.builder().requestFactory(f).build();
  }

  private Map<String, Object> get(String url) {
    try {
      return http.get()
          .uri(java.net.URI.create(url))
          .header("Authorization", "Bearer " + Actor.current().getTokenValue())
          .retrieve()
          .body(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
    } catch (HttpClientErrorException e) {
      throw new org.springframework.web.server.ResponseStatusException(
          e.getStatusCode(), "Reference does not exist or is not accessible");
    } catch (RestClientException e) {
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE, "Reference service unavailable");
    }
  }

  public void patient(UUID id) {
    get(patient + "/api/patients/" + id + "/exists");
  }

  public void staff(UUID id) {
    get(staff + "/api/staff/" + id);
  }

  public void care(UUID id) {
    get(patient + "/api/patients/" + id + "/access");
  }

  public Map<String, Object> prescription(UUID id) {
    return get(medical + "/api/medical-records/prescriptions/" + id);
  }

  public java.util.Set<java.util.UUID> assignedPatients() {
    var response = get(patient + "/api/patients/assigned-ids");
    var values = (java.util.List<?>) response.get("ids");
    return values.stream()
        .map(v -> java.util.UUID.fromString(v.toString()))
        .collect(java.util.stream.Collectors.toSet());
  }

  public void clinician(java.util.UUID id, String role) {
    var account = get(auth + "/api/auth/users/" + id + "/reference");
    if (!Boolean.TRUE.equals(account.get("enabled")) || !role.equals(account.get("role")))
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Clinician account role does not match the assignment");
  }

  public void available(UUID doctor, java.time.Instant start, java.time.Instant end) {
    get(
        staff
            + "/api/staff/"
            + doctor
            + "/availability?startsAt="
            + java.net.URLEncoder.encode(start.toString(), java.nio.charset.StandardCharsets.UTF_8)
            + "&endsAt="
            + java.net.URLEncoder.encode(end.toString(), java.nio.charset.StandardCharsets.UTF_8));
  }

  private String serviceUrl(String name) {
    if (!Set.of(
            "auth",
            "patient",
            "staff",
            "appointment",
            "medical-record",
            "lab",
            "pharmacy",
            "billing",
            "notification",
            "reporting")
        .contains(name)) throw new IllegalArgumentException("Unknown service");
    return environment.getProperty(
        name.toUpperCase(java.util.Locale.ROOT).replace('-', '_') + "_URL",
        "http://" + name + "-service:8080");
  }

  public Map<String, Object> audit(String service, int page) {
    return get(serviceUrl(service) + "/api/audit?page=" + page + "&size=20");
  }

  public Map<String, Object> health(String service) {
    try {
      return get(serviceUrl(service) + "/actuator/health");
    } catch (Exception ex) {
      return Map.of("status", "DOWN");
    }
  }

  public Map<String, Object> appointmentUtilization() {
    return get(serviceUrl("appointment") + "/api/appointments/utilization");
  }

  public Map<String, Object> stockReport() {
    return get(serviceUrl("pharmacy") + "/api/pharmacy");
  }

  public Map<String, Object> expiryReport() {
    return get(serviceUrl("pharmacy") + "/api/pharmacy/alerts");
  }

  public Map<String, Object> balancesReport() {
    return get(serviceUrl("billing") + "/api/billing/balances");
  }
}
