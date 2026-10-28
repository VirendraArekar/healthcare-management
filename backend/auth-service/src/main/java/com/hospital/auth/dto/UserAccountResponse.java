package com.hospital.auth.dto;

import java.time.Instant;
import java.util.*;

public record UserAccountResponse(
    UUID id,
    String email,
    String role,
    boolean enabled,
    UUID patientId,
    UUID staffId,
    long version,
    Instant createdAt) {}
