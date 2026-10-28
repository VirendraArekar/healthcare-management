package com.hospital.lab.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.lab.entity.LaboratoryOrderEntity;
import com.hospital.lab.mapper.LaboratoryOrderMapper;
import com.hospital.lab.repository.jpa.LaboratoryOrderJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class LaboratoryOrderAggregateDao extends JpaAggregateDao<LaboratoryOrderEntity> {
  public LaboratoryOrderAggregateDao(
      LaboratoryOrderJpaRepository repository, LaboratoryOrderMapper mapper, EntityManager em) {
    super(
        "order",
        LaboratoryOrderEntity.class,
        repository,
        mapper,
        em,
        "patientId",
        "test",
        "status");
  }
}
