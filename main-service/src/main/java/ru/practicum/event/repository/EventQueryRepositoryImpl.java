package ru.practicum.event.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class EventQueryRepositoryImpl implements EventQueryRepository {

    private final EntityManager entityManager;

    public EventQueryRepositoryImpl(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<EventFullDto> adminFindEvents(final List<Long> users,
                                              final List<String> states,
                                              final List<Long> categories,
                                              final LocalDateTime rangeStart,
                                              final LocalDateTime rangeEnd,
                                              int from,
                                              int size) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventFullDto> query = cb.createQuery(EventFullDto.class);
        Root<Event> eventTable = query.from(Event.class);

        // Join tables for related entities
        Join<Event, Category> categoryJoin = eventTable.join("category");
        Join<Event, User> initiatorJoin = eventTable.join("initiator");
        Join<Event, Location> locationJoin = eventTable.join("location");

        // Select necessary fields for EventFullDto
        query.select(cb.construct(
                EventFullDto.class,
                eventTable.get("annotation"),
                categoryJoin.get("id"),
                categoryJoin.get("name"),
                eventTable.get("confirmedRequests"),
                eventTable.get("createdOn"),
                eventTable.get("description"),
                eventTable.get("eventDate"),
                eventTable.get("id"),
                initiatorJoin.get("id"),
                initiatorJoin.get("name"),
                locationJoin.get("lat"),
                locationJoin.get("lon"),
                eventTable.get("paid"),
                eventTable.get("participantLimit"),
                eventTable.get("publishedOn"),
                eventTable.get("requestModeration"),
                eventTable.get("state"),
                eventTable.get("title"),
                eventTable.get("views")
        ));

        Predicate predicate = cb.conjunction();

        if (users != null && !users.isEmpty()) {
            predicate = cb.and(predicate, initiatorJoin.get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            predicate = cb.and(predicate, eventTable.get("state").in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            predicate = cb.and(predicate, categoryJoin.get("id").in(categories));
        }

        if (rangeStart != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(eventTable.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(eventTable.get("eventDate"), rangeEnd));
        }

        query.where(predicate);

        TypedQuery<EventFullDto> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }
}
