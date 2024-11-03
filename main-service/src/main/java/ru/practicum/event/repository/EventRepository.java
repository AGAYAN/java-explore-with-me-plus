package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long>, QuerydslPredicateExecutor<Event> {

    @Query("SELECT e FROM Event e " +
            "JOIN e.category c " +
            "JOIN e.location l " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)")
    Page<EventFullDto> adminFindEvents(@Param("users") List<Long> users,
                                       @Param("states") List<String> states,
                                       @Param("categories") List<Long> categories,
                                       @Param("rangeStart") LocalDateTime rangeStart,
                                       @Param("rangeEnd") LocalDateTime rangeEnd,
                                       Pageable pageable);
}
