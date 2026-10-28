package com.hospital.common.repository;

import com.hospital.common.entity.ClinicalResource;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Base persistence adapter shared by the independently owned service databases. */
public abstract class RecordRepository implements ResourceOperations {
  @org.springframework.beans.factory.annotation.Autowired(required = false)
  private java.util.List<com.hospital.common.dao.TypedAggregateDao> typedDaos = java.util.List.of();

  protected com.hospital.common.dao.TypedAggregateDao typed(String kind) {
    return typedDaos.stream().filter(d -> d.kind().equals(kind)).findFirst().orElse(null);
  }

  protected final JdbcTemplate db;
  protected final JsonCodec codec;
  private final ClinicalResourceJpaRepository jpa;

  protected RecordRepository(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    this.db = db;
    this.codec = codec;
    this.jpa = jpa;
  }

  public Map<String, Object> get(String kind, UUID id) {
    if (typed(kind) != null) return typed(kind).get(id);
    return jpa.findById(id)
        .filter(e -> e.getKind().equals(kind))
        .map(this::response)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
  }

  private Map<String, Object> response(ClinicalResource e) {
    var result = new LinkedHashMap<>(e.getData());
    result.put("id", e.getId());
    result.put("version", e.getVersion());
    result.put("createdAt", e.getCreatedAt().toString());
    return result;
  }

  @Transactional
  public Map<String, Object> create(String kind, Map<String, Object> data) {
    if (typed(kind) != null) return typed(kind).create(data);
    return response(jpa.saveAndFlush(new ClinicalResource(UUID.randomUUID(), kind, data)));
  }

  @Transactional
  public Map<String, Object> update(String kind, UUID id, Map<String, Object> data, long version) {
    if (typed(kind) != null) return typed(kind).update(id, data, version);
    var e =
        jpa.findById(id)
            .filter(r -> r.getKind().equals(kind))
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    if (e.getVersion() != version)
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Stale version");
    var clean = new LinkedHashMap<>(data);
    clean.remove("id");
    clean.remove("version");
    clean.remove("createdAt");
    e.setData(clean);
    return response(jpa.saveAndFlush(e));
  }

  public Map<String, Object> dto(Map<String, Object> row) {
    var result = new LinkedHashMap<>(codec.object(row.get("data").toString()));
    result.put("id", row.get("id"));
    result.put("version", row.get("version"));
    result.put("createdAt", row.get("created_at").toString());
    return result;
  }

  public Map<String, Object> list(String kind, int page, int size, String q, String owner) {
    if (typed(kind) != null) return typed(kind).list(page, size, q, owner, null, null);
    if (page < 0 || size < 1 || size > 100 || q.length() > 100)
      throw new IllegalArgumentException();
    String filter = " where kind=? and data::text ilike ? and (?='' or data->>'patientId'=?)";
    var args = new Object[] {kind, "%" + q + "%", owner, owner};
    long total = db.queryForObject("select count(*) from records" + filter, Long.class, args);
    var content =
        db
            .queryForList(
                "select * from records" + filter + " order by created_at desc,id limit ? offset ?",
                kind,
                "%" + q + "%",
                owner,
                owner,
                size,
                (long) page * size)
            .stream()
            .map(this::dto)
            .toList();
    return Map.of("content", content, "totalElements", total, "page", page, "size", size);
  }

  public Map<String, Object> listForPatients(
      String kind, int page, int size, String q, Set<UUID> ids) {
    if (typed(kind) == null) throw new IllegalArgumentException("Typed aggregate required");
    return typed(kind).list(page, size, q, "", ids, null);
  }

  public Map<String, Object> listByStatus(
      String kind, int page, int size, String owner, String status) {
    if (typed(kind) == null) throw new IllegalArgumentException("Typed aggregate required");
    return typed(kind).list(page, size, "", owner, null, status);
  }
}
