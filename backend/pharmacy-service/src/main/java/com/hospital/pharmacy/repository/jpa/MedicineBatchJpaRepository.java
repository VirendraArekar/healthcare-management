package com.hospital.pharmacy.repository.jpa;

import com.hospital.pharmacy.entity.MedicineBatchEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineBatchJpaRepository extends JpaRepository<MedicineBatchEntity, UUID> {}
