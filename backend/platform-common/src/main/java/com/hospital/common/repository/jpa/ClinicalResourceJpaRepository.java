package com.hospital.common.repository.jpa;

import com.hospital.common.entity.ClinicalResource;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicalResourceJpaRepository extends JpaRepository<ClinicalResource, UUID> {}
