package com.hospital.notification.dao.aggregate;

import com.hospital.common.dao.JpaAggregateDao;
import com.hospital.notification.entity.NotificationEntity;
import com.hospital.notification.mapper.NotificationMapper;
import com.hospital.notification.repository.jpa.NotificationJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationAggregateDao extends JpaAggregateDao<NotificationEntity> {
  public NotificationAggregateDao(
      NotificationJpaRepository repository, NotificationMapper mapper, EntityManager em) {
    super(
        "notification",
        NotificationEntity.class,
        repository,
        mapper,
        em,
        "patientId",
        "eventType",
        "message");
  }
}
