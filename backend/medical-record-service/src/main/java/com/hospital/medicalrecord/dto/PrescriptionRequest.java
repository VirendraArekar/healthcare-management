package com.hospital.medicalrecord.dto;

import com.hospital.common.dto.RequestData;
import jakarta.validation.constraints.*;

public record PrescriptionRequest(
    @NotNull java.util.UUID patientId,
    @NotBlank String medicine,
    @Positive int quantity,
    @NotNull @FutureOrPresent java.time.LocalDate validUntil,
    String instructions)
    implements RequestData {}
