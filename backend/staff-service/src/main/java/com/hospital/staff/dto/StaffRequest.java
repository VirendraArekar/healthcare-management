package com.hospital.staff.dto;

import com.hospital.common.dto.RequestData;
import jakarta.validation.constraints.*;

public record StaffRequest(
    @NotBlank String name, @NotBlank String department, @NotBlank String specialty)
    implements RequestData {}
