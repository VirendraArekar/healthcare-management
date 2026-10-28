package com.hospital.medicalrecord.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.medicalrecord.entity.EncounterEntity;
import com.hospital.medicalrecord.mapper.EncounterMapper;
import com.hospital.medicalrecord.repository.jpa.EncounterJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class EncounterAggregateDao extends JpaAggregateDao<EncounterEntity> {
  public EncounterAggregateDao(
      EncounterJpaRepository repository, EncounterMapper mapper, EntityManager em) {
    super(
        "encounter",
        EncounterEntity.class,
        repository,
        mapper,
        em,
        "patientId",
        "diagnosis",
        "treatment");
  }
}
