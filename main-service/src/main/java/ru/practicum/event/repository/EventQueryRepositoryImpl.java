package ru.practicum.event.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;
import ru.practicum.event.enums.SortType;

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

        // Заменяем на поля, которые соответствуют EventShortDto
        query.select(cb.construct(
                EventShortDto.class,
                eventTable.get("annotation"),
                cb.construct(CategoryDto.class,
                        categoryJoin.get("id"),
                        categoryJoin.get("name")
                ),
                eventTable.get("confirmedRequests"),
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

        // Условие: только опубликованные события
        predicate = cb.and(predicate, cb.equal(eventTable.get("state"), "PUBLISHED"));

        // Поиск по тексту в аннотации и описании (без учета регистра)
        if (text != null && !text.trim().isEmpty()) {
            String pattern = "%" + text.trim().toLowerCase() + "%";
            predicate = cb.and(predicate,
                    cb.or(
                            cb.like(cb.lower(eventTable.get("annotation")), pattern),
                            cb.like(cb.lower(eventTable.get("description")), pattern)
                    )
            );
        }

        // Фильтр по категориям
        if (categories != null && !categories.isEmpty()) {
            predicate = cb.and(predicate, categoryJoin.get("id").in(categories));
        }

        // Фильтр по платным/бесплатным событиям
        if (paid != null) {
            predicate = cb.and(predicate, cb.equal(eventTable.get("paid"), paid));
        }

        // Фильтрация по диапазону дат
        LocalDateTime now = LocalDateTime.now();
        if (rangeStart != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(eventTable.get("eventDate"), rangeStart));
        } else {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(eventTable.get("eventDate"), now.plusHours(1L)));
        }
        if (rangeEnd != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(eventTable.get("eventDate"), rangeEnd));
        }

        // Только события с доступными местами (если onlyAvailable == true)
        if (onlyAvailable != null && onlyAvailable) {
            predicate = cb.and(predicate,
                    cb.lessThan(eventTable.get("confirmedRequests"), eventTable.get("participantLimit"))
            );
        }

        query.where(predicate);

        // Сортировка по дате события или по количеству просмотров
        if (sort != null) {
            if (sort.equals(SortType.EVENT_DATE)) {
                query.orderBy(cb.asc(eventTable.get("eventDate")));
            } else if (sort.equals(SortType.VIEWS)) {
                query.orderBy(cb.desc(eventTable.get("views")));
            }
        }

        // Параметры пагинации
        TypedQuery<EventShortDto> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }
}