package com.hospital.medicalrecord.repository.jpa;

import com.hospital.medicalrecord.entity.PrescriptionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionJpaRepository extends JpaRepository<PrescriptionEntity, UUID> {}
