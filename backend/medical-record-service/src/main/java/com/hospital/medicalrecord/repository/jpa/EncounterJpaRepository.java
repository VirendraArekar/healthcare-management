package com.hospital.medicalrecord.repository.jpa;

import com.hospital.medicalrecord.entity.EncounterEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncounterJpaRepository extends JpaRepository<EncounterEntity, UUID> {}
