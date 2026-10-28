package com.hospital.auth.repository.jpa;

import com.hospital.auth.entity.UserAccountEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountEntity, UUID> {}
