package com.hospital.common.dao;

import com.hospital.common.entity.BaseEntity;
import com.hospital.common.mapper.AggregateMapper;
import com.hospital.common.util.Validation;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Typed, paginated aggregate DAO. Client input never becomes a query/property name. */
@Transactional
public abstract class JpaAggregateDao<T extends BaseEntity> implements TypedAggregateDao {
  private final String kind, ownerProperty;
  private final Class<T> entityType;
  private final JpaRepository<T, UUID> repository;
  private final AggregateMapper<T> mapper;
  private final EntityManager em;
  private final List<String> searchProperties;

  protected JpaAggregateDao(
      String kind,
      Class<T> type,
      JpaRepository<T, UUID> repository,
      AggregateMapper<T> mapper,
      EntityManager em,
      String ownerProperty,
      String... search) {
    this.kind = kind;
    this.entityType = type;
    this.repository = repository;
    this.mapper = mapper;
    this.em = em;
    this.ownerProperty = ownerProperty;
    this.searchProperties = List.of(search);
  }

  public String kind() {
    return kind;
  }

  private T find(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
  }

  public Map<String, Object> get(UUID id) {
    return mapper.response(find(id));
  }

  public Map<String, Object> create(Map<String, Object> data) {
    T entity = mapper.create(data);
    entity.initialize();
    return mapper.response(repository.saveAndFlush(entity));
  }

  public Map<String, Object> update(UUID id, Map<String, Object> data, long version) {
    T entity = find(id);
    if (entity.getVersion() != version)
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Record changed; refresh before saving");
    mapper.update(entity, data);
    return mapper.response(repository.saveAndFlush(entity));
  }

  private Predicate[] where(
      CriteriaBuilder cb,
      Root<T> root,
      String query,
      String owner,
      Set<UUID> patients,
      String status) {
    var predicates = new ArrayList<Predicate>();
    if (!query.isBlank() && !searchProperties.isEmpty()) {
      String needle =
          "%"
              + query
                  .toLowerCase(Locale.ROOT)
                  .replace("!", "!!")
                  .replace("%", "!%")
                  .replace("_", "!_")
              + "%";
      predicates.add(
          cb.or(
              searchProperties.stream()
                  .map(key -> cb.like(cb.lower(root.get(key).as(String.class)), needle, '!'))
                  .toArray(Predicate[]::new)));
    }
    if (owner != null && !owner.isBlank()) {
      if (ownerProperty == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      try {
        predicates.add(cb.equal(root.get(ownerProperty), UUID.fromString(owner)));
      } catch (IllegalArgumentException e) {
        predicates.add(cb.disjunction());
      }
    }
    if (patients != null) {
      if (ownerProperty == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      predicates.add(patients.isEmpty() ? cb.disjunction() : root.get(ownerProperty).in(patients));
    }
    if (status != null) predicates.add(cb.equal(root.get("status"), status));
    return predicates.toArray(Predicate[]::new);
  }

  public Map<String, Object> list(
      int page, int size, String query, String owner, Set<UUID> patients, String status) {
    Validation.page(page, size);
    Validation.require(query.length() <= 100, "Search is too long");
    var cb = em.getCriteriaBuilder();
    var countQuery = cb.createQuery(Long.class);
    var countRoot = countQuery.from(entityType);
    countQuery
        .select(cb.count(countRoot))
        .where(where(cb, countRoot, query, owner, patients, status));
    long total = em.createQuery(countQuery).getSingleResult();
    var dataQuery = cb.createQuery(entityType);
    var dataRoot = dataQuery.from(entityType);
    dataQuery
        .select(dataRoot)
        .where(where(cb, dataRoot, query, owner, patients, status))
        .orderBy(cb.desc(dataRoot.get("createdAt")), cb.asc(dataRoot.get("id")));
    var rows =
        em
            .createQuery(dataQuery)
            .setFirstResult(Math.multiplyExact(page, size))
            .setMaxResults(size)
            .getResultList()
            .stream()
            .map(mapper::response)
            .toList();
    return Map.of("content", rows, "totalElements", total, "page", page, "size", size);
  }
}
