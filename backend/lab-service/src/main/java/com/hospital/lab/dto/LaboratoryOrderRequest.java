package com.hospital.lab.dto;

import com.hospital.common.dto.RequestData;
import jakarta.validation.constraints.*;

public record LaboratoryOrderRequest(@NotNull java.util.UUID patientId, @NotBlank String test)
    implements RequestData {}
