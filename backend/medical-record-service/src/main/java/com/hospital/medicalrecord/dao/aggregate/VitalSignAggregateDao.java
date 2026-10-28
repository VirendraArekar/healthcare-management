package com.hospital.medicalrecord.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.medicalrecord.entity.VitalSignEntity;
import com.hospital.medicalrecord.mapper.VitalSignMapper;
import com.hospital.medicalrecord.repository.jpa.VitalSignJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class VitalSignAggregateDao extends JpaAggregateDao<VitalSignEntity> {
  public VitalSignAggregateDao(
      VitalSignJpaRepository repository, VitalSignMapper mapper, EntityManager em) {
    super("vitals", VitalSignEntity.class, repository, mapper, em, null, "observation");
  }
}
