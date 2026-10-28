package com.hospital.common.security;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

public final class Actor {
  private Actor() {}

  public static Jwt current() {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken token) return token.getToken();
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
  }

  public static boolean has(Jwt jwt, String role) {
    return jwt.getClaimAsStringList("roles").contains(role);
  }

  public static UUID patient(Jwt jwt) {
    String id = jwt.getClaimAsString("patientId");
    if (id == null)
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not linked to a patient");
    return UUID.fromString(id);
  }
}
