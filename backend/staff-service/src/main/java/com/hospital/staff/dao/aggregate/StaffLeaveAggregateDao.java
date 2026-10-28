package com.hospital.staff.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.staff.entity.StaffLeaveEntity;
import com.hospital.staff.mapper.StaffLeaveMapper;
import com.hospital.staff.repository.jpa.StaffLeaveJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class StaffLeaveAggregateDao extends JpaAggregateDao<StaffLeaveEntity> {
  public StaffLeaveAggregateDao(
      StaffLeaveJpaRepository repository, StaffLeaveMapper mapper, EntityManager em) {
    super("leave", StaffLeaveEntity.class, repository, mapper, em, null, "status", "reason");
  }
}
