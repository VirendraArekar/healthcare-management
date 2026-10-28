package com.hospital.billing.repository.jpa;

import com.hospital.billing.entity.InvoiceEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, UUID> {}
