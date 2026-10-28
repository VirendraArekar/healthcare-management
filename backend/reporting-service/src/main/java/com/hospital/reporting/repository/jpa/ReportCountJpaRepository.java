package com.hospital.reporting.repository.jpa;

import com.hospital.reporting.entity.ReportCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportCountJpaRepository extends JpaRepository<ReportCountEntity, String> {}
