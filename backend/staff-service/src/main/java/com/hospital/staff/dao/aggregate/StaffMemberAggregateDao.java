package com.hospital.staff.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.staff.entity.StaffMemberEntity;
import com.hospital.staff.mapper.StaffMemberMapper;
import com.hospital.staff.repository.jpa.StaffMemberJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class StaffMemberAggregateDao extends JpaAggregateDao<StaffMemberEntity> {
  public StaffMemberAggregateDao(
      StaffMemberJpaRepository repository, StaffMemberMapper mapper, EntityManager em) {
    super(
        "staff",
        StaffMemberEntity.class,
        repository,
        mapper,
        em,
        null,
        "name",
        "department",
        "specialty");
  }
}
