package com.hospital.auth.service;

import com.hospital.auth.dto.AuthRequests.*;
import com.hospital.auth.repository.AuthRepository;
import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.util.*;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService extends DomainServiceSupport {
  private final AuthRepository repository;
  private final JwtEncoder encoder;
  private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder(12);

  public AuthService(
      AuthRepository repository,
      EventPublisher events,
      AuditService audit,
      JsonCodec codec,
      @Value("${JWT_SECRET}") String secret) {
    super(repository, events, audit, codec);
    this.repository = repository;
    encoder =
        new NimbusJwtEncoder(
            new com.nimbusds.jose.jwk.source.ImmutableSecret<>(
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")));
  }

  @Transactional(noRollbackFor = org.springframework.web.server.ResponseStatusException.class)
  public Object login(Login login) {
    Validation.require(login.email() != null && login.password() != null, "Credentials required");
    var rows = repository.findEnabledUserByEmail(login.email().toLowerCase(Locale.ROOT));
    if (rows.isEmpty())
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Invalid credentials");
    var user = rows.getFirst();
    if (user.get("locked_until") instanceof java.sql.Timestamp locked
        && locked.toInstant().isAfter(Instant.now()))
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Invalid credentials");
    if (!passwords.matches(login.password(), user.get("password_hash").toString())) {
      repository.failedLogin(user.get("id"));
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    repository.clearFailures(user.get("id"));
    audit.record(rows.getFirst().get("id").toString(), "LOGIN", null);
    events.publish(
        "auth.security-event.v1",
        UUID.fromString(rows.getFirst().get("id").toString()),
        Map.of("action", "LOGIN", "outcome", "SUCCESS"));
    return tokens(rows.getFirst());
  }

  @Transactional(noRollbackFor = org.springframework.web.server.ResponseStatusException.class)
  public Object refresh(Refresh request) {
    var rows = repository.lockRefreshToken(hash(request.refreshToken()));
    if (rows.isEmpty())
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    var row = rows.getFirst();
    if (Boolean.TRUE.equals(row.get("revoked"))) {
      repository.revokeFamily(row.get("family_id"));
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Refresh token reuse detected");
    }
    if (((java.sql.Timestamp) row.get("expires_at")).toInstant().isBefore(Instant.now()))
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Refresh token expired");
    repository.revokeRefreshToken(rows.getFirst().get("refresh_id"));
    var result = (Map<String, Object>) tokens(rows.getFirst());
    repository.setFamily(
        hash(result.get("refreshToken").toString()), rows.getFirst().get("family_id"));
    return result;
  }

  @Transactional
  public Object logout(Refresh request, Jwt jwt) {
    repository.revokeUserToken(hash(request.refreshToken()), UUID.fromString(jwt.getSubject()));
    audit.record(jwt.getSubject(), "LOGOUT", null);
    events.publish(
        "auth.security-event.v1",
        UUID.fromString(jwt.getSubject()),
        Map.of("action", "LOGOUT", "outcome", "SUCCESS"));
    return Map.of("status", "SIGNED_OUT");
  }

  public Object me(Jwt jwt) {
    var profile = new LinkedHashMap<String, Object>();
    profile.put("id", jwt.getSubject());
    profile.put("roles", jwt.getClaimAsStringList("roles"));
    profile.put("email", jwt.getClaimAsString("email"));
    if (jwt.hasClaim("patientId")) profile.put("patientId", jwt.getClaimAsString("patientId"));
    if (jwt.hasClaim("staffId")) profile.put("staffId", jwt.getClaimAsString("staffId"));
    return profile;
  }

  @PreAuthorize("hasRole('ADMIN')")
  public Object users() {
    return repository.findUsers();
  }

  @PreAuthorize("hasRole('ADMIN')")
  public Object user(Map<String, Object> b) {
    String email = Validation.required(b, "email").toLowerCase(Locale.ROOT),
        password = Validation.required(b, "password"),
        role = Validation.required(b, "role");
    Validation.require(
        password.length() >= 12
            && Set.of(
                    "ADMIN",
                    "DOCTOR",
                    "NURSE",
                    "RECEPTIONIST",
                    "PHARMACIST",
                    "LAB_TECHNICIAN",
                    "BILLING_STAFF",
                    "PATIENT")
                .contains(role),
        "Invalid role or password length");
    UUID id = UUID.randomUUID();
    Object patient = b.get("patientId");
    Object staff = b.get("staffId");
    if (staff != null) directory.staff(UUID.fromString(staff.toString()));
    if (patient != null) directory.patient(UUID.fromString(patient.toString()));
    repository.insertUser(
        id,
        email,
        passwords.encode(password),
        role,
        patient == null ? null : UUID.fromString(patient.toString()),
        staff == null ? null : UUID.fromString(staff.toString()));
    return Map.of("id", id);
  }

  private Object tokens(Map<String, Object> user) {
    Instant now = Instant.now();
    var builder =
        JwtClaimsSet.builder()
            .issuer("hospital-platform")
            .subject(user.get("id").toString())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(600))
            .claim("roles", List.of(user.get("role").toString()))
            .claim("email", user.get("email").toString());
    if (user.get("patient_id") != null)
      builder.claim("patientId", user.get("patient_id").toString());
    if (user.get("staff_id") != null) builder.claim("staffId", user.get("staff_id").toString());
    String access =
        encoder
            .encode(
                JwtEncoderParameters.from(
                    JwsHeader.with(MacAlgorithm.HS256).build(), builder.build()))
            .getTokenValue();
    byte[] random = new byte[48];
    new SecureRandom().nextBytes(random);
    String refresh = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    repository.insertRefreshToken(
        UUID.randomUUID(),
        user.get("id"),
        hash(refresh),
        java.sql.Timestamp.from(now.plusSeconds(604800)));
    return Map.of(
        "accessToken", access, "refreshToken", refresh, "expiresIn", 600, "role", user.get("role"));
  }

  public static String hash(String token) {
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256")
                  .digest(Objects.requireNonNull(token).getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid token");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  public Object account(UUID id, Map<String, Object> b, Jwt jwt) {
    Validation.require(
        !id.toString().equals(jwt.getSubject()),
        "Use another administrator to change this account");
    String role = Validation.required(b, "role");
    com.hospital.auth.model.Role.valueOf(role);
    var current = repository.accountReference(id);
    Validation.require(!current.isEmpty(), "Account not found");
    Object
        staffValue =
            b.containsKey("staffId") ? b.get("staffId") : current.getFirst().get("staff_id"),
        patientValue =
            b.containsKey("patientId") ? b.get("patientId") : current.getFirst().get("patient_id");
    UUID
        staff =
            staffValue == null || staffValue.toString().isBlank()
                ? null
                : UUID.fromString(staffValue.toString()),
        patient =
            patientValue == null || patientValue.toString().isBlank()
                ? null
                : UUID.fromString(patientValue.toString());
    if (staff != null) directory.staff(staff);
    if (patient != null) directory.patient(patient);
    long version = Long.parseLong(Validation.required(b, "version"));
    if (!repository.updateAccount(
        id, role, Boolean.parseBoolean(Validation.required(b, "enabled")), version, staff, patient))
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.CONFLICT, "Account changed; refresh before editing");
    audit.record(jwt.getSubject(), "UPDATE_ACCOUNT", id);
    return Map.of("id", id, "role", role);
  }

  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
  public Object reference(UUID id) {
    var rows = repository.accountReference(id);
    if (rows.isEmpty())
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.NOT_FOUND, "Account not found");
    return rows.getFirst();
  }

  public Object clinicians() {
    return Map.of("content", repository.clinicians());
  }
}
