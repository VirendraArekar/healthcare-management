package com.hospital.pharmacy.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.pharmacy.entity.MedicineEntity;
import com.hospital.pharmacy.mapper.MedicineMapper;
import com.hospital.pharmacy.repository.jpa.MedicineJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class MedicineAggregateDao extends JpaAggregateDao<MedicineEntity> {
  public MedicineAggregateDao(
      MedicineJpaRepository repository, MedicineMapper mapper, EntityManager em) {
    super(
        "medicine",
        MedicineEntity.class,
        repository,
        mapper,
        em,
        null,
        "name",
        "strength",
        "manufacturer");
  }
}
