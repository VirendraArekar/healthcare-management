package com.hospital.common.service;

import com.hospital.common.repository.AuditRepository;
import com.hospital.common.util.Validation;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditService {
  private final AuditRepository repository;

  public AuditService(AuditRepository repository) {
    this.repository = repository;
  }

  public void record(String actor, String action, UUID id) {
    repository.save(actor, action, id);
  }

  public Object page(int page, int size) {
    Validation.page(page, size);
    return repository.page(page, size);
  }
}
