package com.hospital.patient.repository.jpa;

import com.hospital.patient.entity.PatientEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, UUID> {}
