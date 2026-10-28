package com.hospital.staff.repository.jpa;

import com.hospital.staff.entity.WorkingScheduleEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingScheduleJpaRepository extends JpaRepository<WorkingScheduleEntity, UUID> {}
