package com.hospital.staff.repository.jpa;

import com.hospital.staff.entity.DepartmentEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentJpaRepository extends JpaRepository<DepartmentEntity, UUID> {}
