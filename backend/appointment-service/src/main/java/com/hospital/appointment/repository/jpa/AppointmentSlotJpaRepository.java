package com.hospital.appointment.repository.jpa;

import com.hospital.appointment.entity.AppointmentSlotEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentSlotJpaRepository extends JpaRepository<AppointmentSlotEntity, UUID> {}
