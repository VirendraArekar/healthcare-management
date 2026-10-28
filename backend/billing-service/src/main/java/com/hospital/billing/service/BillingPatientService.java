package com.hospital.billing.service;

import com.hospital.billing.repository.BillingRepository;
import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasRole('PATIENT')")
@Service
@Transactional
public class BillingPatientService extends DomainServiceSupport {
  private final BillingRepository repository;

  public BillingPatientService(
      BillingRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  public Object mine(Jwt jwt, int page) {
    String owner = Objects.requireNonNullElse(jwt.getClaimAsString("patientId"), "unbound");
    Validation.require(page >= 0, "Pagination");
    return repository.findPatientRecords(owner, page);
  }
}
