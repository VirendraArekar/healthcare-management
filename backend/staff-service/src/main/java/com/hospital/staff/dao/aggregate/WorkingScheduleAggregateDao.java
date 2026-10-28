package com.hospital.staff.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.staff.entity.WorkingScheduleEntity;
import com.hospital.staff.mapper.WorkingScheduleMapper;
import com.hospital.staff.repository.jpa.WorkingScheduleJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class WorkingScheduleAggregateDao extends JpaAggregateDao<WorkingScheduleEntity> {
  public WorkingScheduleAggregateDao(
      WorkingScheduleJpaRepository repository, WorkingScheduleMapper mapper, EntityManager em) {
    super(
        "schedule",
        WorkingScheduleEntity.class,
        repository,
        mapper,
        em,
        null,
        "dayOfWeek",
        "status");
  }
}
