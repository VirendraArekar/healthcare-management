package com.hospital.staff.repository.jpa;

import com.hospital.staff.entity.StaffMemberEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffMemberJpaRepository extends JpaRepository<StaffMemberEntity, UUID> {}
