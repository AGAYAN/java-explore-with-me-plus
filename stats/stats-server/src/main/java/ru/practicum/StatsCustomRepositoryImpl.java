package ru.practicum;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndPointHit;
import ru.practicum.model.ViewStats;

@Repository
public class StatsCustomRepositoryImpl implements StatsCustomRepository {

  private final EntityManager entityManager;

  public StatsCustomRepositoryImpl(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<ViewStats> getStats(final LocalDateTime start, final LocalDateTime end,
                                  final List<String> uris, final boolean unique) {

    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<ViewStats> query = cb.createQuery(ViewStats.class);
    // FROM
    final Root<EndPointHit> endPointHitTable = query.from(EndPointHit.class);
    // SELECT app, uri, COUNT(DISTINCT ip)
    final Expression<Long> hitsCount = unique
        ? cb.countDistinct(endPointHitTable.get("ip"))
        : cb.count(endPointHitTable.get("ip"));
    query.multiselect(
        endPointHitTable.get("app"),
        endPointHitTable.get("uri"),
        hitsCount);
    // Building conditions: request_time BETWEEN :start AND :end
    final Predicate betweenDates = cb.between(endPointHitTable.get("requestTime"), start, end);
    // Building conditions: uri IN (:uris)
    Predicate uriInList = null;
    if (uris != null && !uris.isEmpty()) {
      uriInList = endPointHitTable.get("uri").in(uris);
    }
    // Apply WHERE {conditions}
    query.where(uriInList != null ? cb.and(betweenDates, uriInList) : betweenDates);
    // GROUP BY app, uri
    query.groupBy(endPointHitTable.get("app"), endPointHitTable.get("uri"));
    // ORDER BY hitsCount DESC
    query.orderBy(cb.desc(hitsCount));

    return entityManager.createQuery(query).getResultList();
  }

}
