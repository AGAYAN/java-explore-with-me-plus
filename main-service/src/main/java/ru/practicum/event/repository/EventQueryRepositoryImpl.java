package ru.practicum.event.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.catalina.connector.Request;
import org.springframework.stereotype.Repository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;
import ru.practicum.event.enums.SortType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    ///TODO Ждать пока будет готова реализация с CONFIRMED request
    @Override
    public List<EventShortDto> publicGetEvents(final String text,
                                               final List<Long> categories,
                                               final Boolean paid,
                                               final LocalDateTime rangeStart,
                                               final LocalDateTime rangeEnd,
                                               final Boolean onlyAvailable,
                                               final SortType sort,
                                               final int from,
                                               final int size) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventShortDto> query = cb.createQuery(EventShortDto.class);
        Root<Event> eventTable = query.from(Event.class);

        Join<Event, Category> categoryJoin = eventTable.join("category");
        Join<Event, User> initiatorJoin = eventTable.join("initiator");

        query.select(cb.construct(
                EventShortDto.class,
                eventTable.get("annotation"),
                cb.construct(CategoryDto.class,
                        categoryJoin.get("id"),
                        categoryJoin.get("name")
                ),
                eventTable.get("eventDate"),
                eventTable.get("id"),
                cb.construct(UserShortDto.class,
                        initiatorJoin.get("id"),
                        initiatorJoin.get("name")
                ),
                eventTable.get("paid"),
                eventTable.get("title"),
                eventTable.get("views")
        ));

        Predicate predicate = cb.conjunction();
        predicate = cb.and(predicate, cb.equal(eventTable.get("state"), State.PUBLISHED));

        if (text != null && !text.trim().isEmpty()) {
            String pattern = "%" + text.trim().toLowerCase() + "%";
            predicate = cb.and(predicate,
                    cb.or(
                            cb.like(cb.lower(eventTable.get("annotation")), pattern),
                            cb.like(cb.lower(eventTable.get("description")), pattern)
                    )
            );
        }

        if (categories != null && !categories.isEmpty()) {
            predicate = cb.and(predicate, categoryJoin.get("id").in(categories));
        }

        if (paid != null) {
            predicate = cb.and(predicate, cb.equal(eventTable.get("paid"), paid));
        }

        LocalDateTime now = LocalDateTime.now();
        if (rangeStart != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(eventTable.get("eventDate"), rangeStart));
        } else {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(eventTable.get("eventDate"), now));
        }
        if (rangeEnd != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(eventTable.get("eventDate"), rangeEnd));
        }

        query.where(predicate);

        if (sort != null) {
            if (sort.equals(SortType.EVENT_DATE)) {
                query.orderBy(cb.asc(eventTable.get("eventDate")));
            } else if (sort.equals(SortType.VIEWS)) {
                query.orderBy(cb.desc(eventTable.get("views")));
            }
        }

        TypedQuery<EventShortDto> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);

        List<EventShortDto> resultList = typedQuery.getResultList();

        List<Long> eventIds = resultList.stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequests(eventIds);

        resultList.forEach(event -> event.setConfirmedRequests(Math.
                toIntExact(confirmedRequestsMap.getOrDefault(event.getId(), 0L))));


        return resultList;
    }

    private Map<Long, Long> getConfirmedRequests(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // Запрос для подсчета confirmedRequests по каждому событию
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> countQuery = cb.createTupleQuery();
        Root<Request> requestTable = countQuery.from(Request.class);

        countQuery.multiselect(
                requestTable.get("event").get("id"),
                cb.count(requestTable).alias("confirmedCount")
        );
        countQuery.where(
                requestTable.get("event").get("id").in(eventIds)//,
//                cb.equal(requestTable.get("status"), StatusRequest.CONFIRMED)
        );
        countQuery.groupBy(requestTable.get("event").get("id"));

        List<Tuple> results = entityManager.createQuery(countQuery).getResultList();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),
                        tuple -> tuple.get(1, Long.class)
                ));
    }

}