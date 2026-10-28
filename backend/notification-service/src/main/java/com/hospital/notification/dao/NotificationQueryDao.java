package com.hospital.notification.dao;

import com.hospital.common.util.Validation;
import com.hospital.notification.entity.NotificationEntity;
import com.hospital.notification.mapper.NotificationMapper;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationQueryDao {
  private final EntityManager em;
  private final NotificationMapper mapper;

  public NotificationQueryDao(EntityManager em, NotificationMapper mapper) {
    this.em = em;
    this.mapper = mapper;
  }

  private Predicate[] filters(
      CriteriaBuilder cb,
      Root<NotificationEntity> root,
      Set<String> prefixes,
      UUID patient,
      Set<UUID> assigned) {
    var predicates = new ArrayList<Predicate>();
    if (!prefixes.isEmpty())
      predicates.add(
          cb.or(
              prefixes.stream()
                  .map(prefix -> cb.like(root.get("eventType"), prefix + "%"))
                  .toArray(Predicate[]::new)));
    if (patient != null) predicates.add(cb.equal(root.get("patientId"), patient));
    if (assigned != null)
      predicates.add(assigned.isEmpty() ? cb.disjunction() : root.get("patientId").in(assigned));
    return predicates.toArray(Predicate[]::new);
  }

  public Map<String, Object> list(
      int page, int size, Set<String> prefixes, UUID patient, Set<UUID> assigned) {
    Validation.page(page, size);
    var cb = em.getCriteriaBuilder();
    var count = cb.createQuery(Long.class);
    var cr = count.from(NotificationEntity.class);
    count.select(cb.count(cr)).where(filters(cb, cr, prefixes, patient, assigned));
    long total = em.createQuery(count).getSingleResult();
    var query = cb.createQuery(NotificationEntity.class);
    var row = query.from(NotificationEntity.class);
    query
        .select(row)
        .where(filters(cb, row, prefixes, patient, assigned))
        .orderBy(cb.desc(row.get("createdAt")));
    var content =
        em
            .createQuery(query)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .getResultList()
            .stream()
            .map(mapper::response)
            .toList();
    return Map.of("content", content, "totalElements", total, "page", page, "size", size);
  }
}
