package com.hospital.lab.repository.jpa;

import com.hospital.lab.entity.LaboratoryTestEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaboratoryTestJpaRepository extends JpaRepository<LaboratoryTestEntity, UUID> {}
