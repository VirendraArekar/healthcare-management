package com.hospital.staff.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.staff.entity.DepartmentEntity;
import com.hospital.staff.mapper.DepartmentMapper;
import com.hospital.staff.repository.jpa.DepartmentJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentAggregateDao extends JpaAggregateDao<DepartmentEntity> {
  public DepartmentAggregateDao(
      DepartmentJpaRepository repository, DepartmentMapper mapper, EntityManager em) {
    super("department", DepartmentEntity.class, repository, mapper, em, null, "name");
  }
}
