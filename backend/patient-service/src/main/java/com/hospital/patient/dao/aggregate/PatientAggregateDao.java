package com.hospital.patient.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.patient.entity.PatientEntity;
import com.hospital.patient.mapper.PatientMapper;
import com.hospital.patient.repository.jpa.PatientJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class PatientAggregateDao extends JpaAggregateDao<PatientEntity> {
  public PatientAggregateDao(
      PatientJpaRepository repository, PatientMapper mapper, EntityManager em) {
    super(
        "patient",
        PatientEntity.class,
        repository,
        mapper,
        em,
        "id",
        "firstName",
        "lastName",
        "email",
        "patientNumber");
  }
}
