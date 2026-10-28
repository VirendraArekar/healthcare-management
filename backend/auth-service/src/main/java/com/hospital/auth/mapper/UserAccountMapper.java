package com.hospital.auth.mapper;

import com.hospital.auth.dto.UserAccountResponse;
import com.hospital.auth.entity.UserAccountEntity;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {
  public UserAccountResponse response(UserAccountEntity user) {
    return new UserAccountResponse(
        user.getId(),
        user.getEmail(),
        user.getRole(),
        user.isEnabled(),
        user.getPatientId(),
        user.getStaffId(),
        user.getVersion(),
        user.getCreatedAt());
  }
}
