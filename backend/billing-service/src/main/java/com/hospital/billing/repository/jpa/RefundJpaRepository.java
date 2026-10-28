package com.hospital.billing.repository.jpa;

import com.hospital.billing.entity.RefundEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundJpaRepository extends JpaRepository<RefundEntity, UUID> {}
