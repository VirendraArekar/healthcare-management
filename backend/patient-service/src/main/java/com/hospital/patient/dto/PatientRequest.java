package com.hospital.patient.dto;

import com.hospital.common.dto.RequestData;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PatientRequest(
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName,
    @NotNull @PastOrPresent LocalDate dateOfBirth,
    @NotBlank @Email String email,
    @Size(max = 30) String phone,
    @Size(max = 500) String emergencyContact,
    @Size(max = 1000) String allergies,
    Long version,
    @Pattern(regexp = "ACTIVE|INACTIVE") String status)
    implements RequestData {}
