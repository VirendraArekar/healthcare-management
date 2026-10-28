package com.hospital.staff.repository.jpa;

import com.hospital.staff.entity.StaffLeaveEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffLeaveJpaRepository extends JpaRepository<StaffLeaveEntity, UUID> {}
