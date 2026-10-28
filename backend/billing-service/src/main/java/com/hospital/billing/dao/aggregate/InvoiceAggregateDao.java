package com.hospital.billing.dao.aggregate;

import com.hospital.billing.entity.InvoiceEntity;
import com.hospital.billing.mapper.InvoiceMapper;
import com.hospital.billing.repository.jpa.InvoiceJpaRepository;
import com.hospital.common.dao.JpaAggregateDao;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceAggregateDao extends JpaAggregateDao<InvoiceEntity> {
  public InvoiceAggregateDao(
      InvoiceJpaRepository repository, InvoiceMapper mapper, EntityManager em) {
    super(
        "invoice", InvoiceEntity.class, repository, mapper, em, "patientId", "status", "currency");
  }
}
