package com.hospital.billing.dao.aggregate;

import com.hospital.billing.entity.RefundEntity;
import com.hospital.billing.mapper.RefundMapper;
import com.hospital.billing.repository.jpa.RefundJpaRepository;
import com.hospital.common.dao.JpaAggregateDao;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class RefundAggregateDao extends JpaAggregateDao<RefundEntity> {
  public RefundAggregateDao(RefundJpaRepository repository, RefundMapper mapper, EntityManager em) {
    super("refund", RefundEntity.class, repository, mapper, em, null, "status", "reason");
  }
}
