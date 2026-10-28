package com.hospital.appointment.dto;

import com.hospital.common.dto.RequestData;
import jakarta.validation.constraints.*;

public record BookingRequest(@NotNull java.util.UUID patientId, @NotNull java.util.UUID slotId)
    implements RequestData {}
