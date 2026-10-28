package com.hospital.billing.dto;

import com.hospital.common.dto.RequestData;
import jakarta.validation.constraints.*;

public record InvoiceRequest(
    @NotNull java.util.UUID patientId,
    @Digits(integer = 12, fraction = 2) java.math.BigDecimal subtotal,
    @NotNull @DecimalMin("0") @DecimalMax("100") java.math.BigDecimal taxPercent,
    @NotNull @DecimalMin("0") @Digits(integer = 12, fraction = 2) java.math.BigDecimal discount,
    java.util.List<java.util.Map<String, Object>> lineItems)
    implements RequestData {}
