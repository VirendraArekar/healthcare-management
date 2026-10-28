package com.hospital.appointment.dto;

import com.hospital.common.dto.RequestData;
import jakarta.validation.constraints.*;

public record SlotRequest(
    @NotNull java.util.UUID doctorId,
    @NotNull @Future java.time.Instant startsAt,
    @NotNull @Future java.time.Instant endsAt)
    implements RequestData {}
