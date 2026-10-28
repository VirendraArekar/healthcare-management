package com.hospital.lab.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.lab.repository.LaboratoryRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TestCatalogService extends DomainServiceSupport {
  private final LaboratoryRepository repository;

  public TestCatalogService(
      LaboratoryRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  public Object list(int page) {
    Validation.page(page, 20);
    if (page == 0)
      return referenceCache.get(
          "catalog:test-catalog",
          java.time.Duration.ofMinutes(5),
          () -> repository.list("test-catalog", 0, 20, "", ""));
    return repository.list("test-catalog", page, 20, "", "");
  }

  public Object create(Map<String, Object> b) {
    Validation.required(b, "name");
    Validation.required(b, "referenceRange");
    Validation.required(b, "unit");
    var result = create("test-catalog", b, null);
    referenceCache.invalidate("catalog:test-catalog");
    return result;
  }

  public Object update(java.util.UUID id, Map<String, Object> b) {
    var current = repository.get("test-catalog", id);
    long version = Long.parseLong(Validation.required(b, "version"));
    current.putAll(b);
    Validation.required(current, "name");
    var response = update("test-catalog", id, current, version, null);
    referenceCache.invalidate("catalog:test-catalog");
    return response;
  }
}
