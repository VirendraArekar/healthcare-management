package com.hospital.appointment.repository.jpa;

import com.hospital.appointment.entity.AppointmentEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, UUID> {}
