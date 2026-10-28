package com.hospital.lab.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.lab.entity.LaboratoryTestEntity;
import com.hospital.lab.mapper.LaboratoryTestMapper;
import com.hospital.lab.repository.jpa.LaboratoryTestJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class LaboratoryTestAggregateDao extends JpaAggregateDao<LaboratoryTestEntity> {
  public LaboratoryTestAggregateDao(
      LaboratoryTestJpaRepository repository, LaboratoryTestMapper mapper, EntityManager em) {
    super(
        "test-catalog",
        LaboratoryTestEntity.class,
        repository,
        mapper,
        em,
        null,
        "name",
        "specimenType");
  }
}
