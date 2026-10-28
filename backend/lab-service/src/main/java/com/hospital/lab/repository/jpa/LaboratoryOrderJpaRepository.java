package com.hospital.lab.repository.jpa;

import com.hospital.lab.entity.LaboratoryOrderEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaboratoryOrderJpaRepository extends JpaRepository<LaboratoryOrderEntity, UUID> {}
