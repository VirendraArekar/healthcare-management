package com.hospital.auth.config;

import com.hospital.auth.repository.AuthRepository;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Profile("local")
public class DemoUsers {
  @Bean
  CommandLineRunner seed(
      AuthRepository repository,
      @Value("${hospital.demo-enabled:false}") boolean enabled,
      @Value("${DEMO_PASSWORD:}") String password) {
    return args -> {
      if (!enabled) return;
      if (password.length() < 12)
        throw new IllegalArgumentException("Set DEMO_PASSWORD to at least 12 characters");
      var hash = new BCryptPasswordEncoder(12).encode(password);
      for (String role :
          List.of(
              "ADMIN",
              "DOCTOR",
              "NURSE",
              "RECEPTIONIST",
              "PHARMACIST",
              "LAB_TECHNICIAN",
              "BILLING_STAFF",
              "PATIENT")) {
        UUID id = UUID.nameUUIDFromBytes(role.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        repository.insertDemoUser(id, role.toLowerCase(Locale.ROOT) + "@demo.hospital", hash, role);
      }
      repository.insertDemoUser(
          UUID.nameUUIDFromBytes(
              "LAB_TECHNICIAN_2".getBytes(java.nio.charset.StandardCharsets.UTF_8)),
          "lab_technician2@demo.hospital",
          hash,
          "LAB_TECHNICIAN");
      repository.insertDemoUser(
          UUID.nameUUIDFromBytes("DOCTOR_2".getBytes(java.nio.charset.StandardCharsets.UTF_8)),
          "doctor2@demo.hospital",
          hash,
          "DOCTOR");
      repository.insertDemoUser(
          UUID.nameUUIDFromBytes("PATIENT_2".getBytes(java.nio.charset.StandardCharsets.UTF_8)),
          "patient2@demo.hospital",
          hash,
          "PATIENT");
    };
  }
}
