package ru.practicum.event.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.model.Event;

public interface EventRepository extends JpaRepository<Event,Long>, QuerydslPredicateExecutor<Event> {

  Page<Event> findAllByInitiatorId(Long initiatorId, PageRequest page);

  @Query()
  Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

}
