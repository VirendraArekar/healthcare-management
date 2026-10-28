package com.hospital.medicalrecord.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.medicalrecord.entity.PrescriptionEntity;
import com.hospital.medicalrecord.mapper.PrescriptionMapper;
import com.hospital.medicalrecord.repository.jpa.PrescriptionJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class PrescriptionAggregateDao extends JpaAggregateDao<PrescriptionEntity> {
  public PrescriptionAggregateDao(
      PrescriptionJpaRepository repository, PrescriptionMapper mapper, EntityManager em) {
    super(
        "prescription",
        PrescriptionEntity.class,
        repository,
        mapper,
        em,
        "patientId",
        "medicine",
        "status");
  }
}
