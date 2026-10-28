package com.hospital.medicalrecord.dto;

import com.hospital.common.dto.RequestData;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record VitalSignsRequest(
    @NotBlank String observation,
    @DecimalMin("0") BigDecimal systolic,
    @DecimalMin("0") BigDecimal diastolic,
    @DecimalMin("0") BigDecimal pulse,
    BigDecimal temperature,
    @DecimalMin("0") @DecimalMax("100") BigDecimal oxygenSaturation,
    @DecimalMin("0") BigDecimal weight)
    implements RequestData {}
