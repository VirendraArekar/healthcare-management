package com.hospital.pharmacy.repository.jpa;

import com.hospital.pharmacy.entity.MedicineEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineJpaRepository extends JpaRepository<MedicineEntity, UUID> {}
