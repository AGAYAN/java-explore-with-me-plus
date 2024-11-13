package ru.practicum.event.repository;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventWithRequestCount;

public interface EventRepository extends JpaRepository<Event, Long>, EventQueryRepository {

  Page<Event> findAllByInitiatorId(Long initiatorId, PageRequest page);

  @Query("""
      SELECT e AS event, COUNT(r.id) AS confirmedRequests
      FROM Event e
      JOIN FETCH e.category c
      JOIN FETCH e.initiator u
      LEFT JOIN FETCH ParticipationRequest r ON r.event.id = e.id  AND r.status = 'CONFIRMED'
      WHERE e.id = :eventId
        AND e.initiator.id = :initiatorId
      GROUP BY e, c.id, u.id
      """)
  Optional<EventWithRequestCount> findByIdAndInitiatorId(@Param("eventId") Long eventId,
                                                         @Param("initiatorId") Long initiatorId);

  Optional<Event> findByIdAndState(Long id, State state);

  boolean existsByIdAndInitiatorId(Long eventId, Long userId);

  boolean existsByCategoryId(Long id);

  Set<Event> findAllDistinctByIdIn(Set<Long> eventIds);
}

