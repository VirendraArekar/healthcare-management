package com.hospital.medicalrecord.repository.jpa;

import com.hospital.medicalrecord.entity.VitalSignEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VitalSignJpaRepository extends JpaRepository<VitalSignEntity, UUID> {}
