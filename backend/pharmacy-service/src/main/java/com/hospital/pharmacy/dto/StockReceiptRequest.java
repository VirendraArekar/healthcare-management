package com.hospital.pharmacy.dto;

import com.hospital.common.dto.RequestData;
import jakarta.validation.constraints.*;

public record StockReceiptRequest(
    @NotBlank String medicine,
    @Positive int quantity,
    @NotNull @Future java.time.LocalDate expiresOn)
    implements RequestData {}
