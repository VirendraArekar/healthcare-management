package com.hospital.pharmacy.service;

import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.pharmacy.repository.PharmacyRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MedicineService extends DomainServiceSupport {
  private final PharmacyRepository repository;

  public MedicineService(
      PharmacyRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  public Object list(int page) {
    Validation.page(page, 20);
    if (page == 0)
      return referenceCache.get(
          "catalog:medicine",
          java.time.Duration.ofMinutes(5),
          () -> repository.list("medicine", 0, 20, "", ""));
    return repository.list("medicine", page, 20, "", "");
  }

  public Object create(Map<String, Object> b) {
    Validation.required(b, "name");
    Validation.required(b, "strength");
    Validation.required(b, "unit");
    var result = create("medicine", b, null);
    referenceCache.invalidate("catalog:medicine");
    return result;
  }

  public Object update(java.util.UUID id, Map<String, Object> b) {
    var current = repository.get("medicine", id);
    long version = Long.parseLong(Validation.required(b, "version"));
    current.putAll(b);
    Validation.required(current, "name");
    var response = update("medicine", id, current, version, null);
    referenceCache.invalidate("catalog:medicine");
    return response;
  }
}
