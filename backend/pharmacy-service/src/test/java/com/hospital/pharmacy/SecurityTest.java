package com.hospital.pharmacy;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hospital.common.exception.Errors;
import com.hospital.common.security.SecurityConfig;
import com.hospital.pharmacy.controller.PharmacyController;
import com.hospital.pharmacy.service.PharmacyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PharmacyController.class)
@Import({SecurityConfig.class, Errors.class})
@TestPropertySource(properties = "JWT_SECRET=unit-test-only-secret-at-least-thirty-two-characters")
class SecurityTest {
  @Autowired MockMvc mvc;
  @MockitoBean PharmacyService service;

  @Test
  void anonymousCannotRead() throws Exception {
    mvc.perform(get("/api/pharmacy")).andExpect(status().isUnauthorized());
  }

  @Test
  void patientCannotCreate() throws Exception {
    mvc.perform(
            post("/api/pharmacy")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PATIENT")))
                .contentType("application/json")
                .content(
                    "{\"medicine\":\"Synthetic\",\"quantity\":1,\"expiresOn\":\"2099-01-01\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void invalidBearerTokenRejected() throws Exception {
    mvc.perform(get("/api/pharmacy").header("Authorization", "Bearer invalid"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void invalidDtoRejected() throws Exception {
    mvc.perform(
            post("/api/pharmacy")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType("application/json")
                .content("{}"))
        .andExpect(status().isBadRequest());
  }
}
