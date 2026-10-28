package com.hospital.medicalrecord.dto;

import com.hospital.common.dto.RequestData;
import jakarta.validation.constraints.*;

public record EncounterRequest(
    @NotNull java.util.UUID patientId,
    @NotBlank String diagnosis,
    @NotBlank String notes,
    @NotBlank String treatment,
    Long version)
    implements RequestData {}
