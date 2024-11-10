package ru.practicum.event.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.StatusRequest;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;
import ru.practicum.event.enums.SortType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class EventQueryRepositoryImpl implements EventQueryRepository {

    private final EntityManager entityManager;
    private final StatsClient statsClient;

    public EventQueryRepositoryImpl(final EntityManager entityManager, StatsClient statsClient) {
        this.entityManager = entityManager;
        this.statsClient = statsClient;
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
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Event> eventTable = query.from(Event.class);

        Join<Event, Category> categoryJoin = eventTable.join("category");
        Join<Event, User> initiatorJoin = eventTable.join("initiator");
        Join<Event, Location> locationJoin = eventTable.join("location");

        // Выбираем необходимые поля с алиасами для использования в маппинге
        query.multiselect(
                eventTable.get("annotation").alias("annotation"),
                categoryJoin.get("id").alias("categoryId"),
                categoryJoin.get("name").alias("categoryName"),
                eventTable.get("eventDate").alias("eventDate"),
                eventTable.get("id").alias("eventId"),
                initiatorJoin.get("id").alias("initiatorId"),
                initiatorJoin.get("name").alias("initiatorName"),
                eventTable.get("paid").alias("paid"),
                eventTable.get("title").alias("title"),
                eventTable.get("description").alias("description"),
                eventTable.get("createdOn").alias("createdOn"),
                eventTable.get("publishedOn").alias("publishedOn"),
                eventTable.get("participantLimit").alias("participantLimit"),
                eventTable.get("state").alias("state"),
                locationJoin.get("lat").alias("lat"),
                locationJoin.get("lon").alias("lon"),
                eventTable.get("requestModeration").alias("requestModeration")
        );

        // Создаем предикаты для условий запроса
        Predicate predicate = cb.conjunction();

        if (users != null && !users.isEmpty()) {
            predicate = cb.and(predicate, initiatorJoin.get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            // Преобразуем строки в перечисления State
            List<State> stateEnums = states.stream()
                    .map(State::valueOf) // Преобразуем строку в перечисление
                    .collect(Collectors.toList());

            // Фильтруем по перечислениям, которые будут сравниваться со строковыми значениями в базе данных
            predicate = cb.and(predicate, eventTable.get("state").in(stateEnums));
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

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);

        List<Tuple> tuples = typedQuery.getResultList();

        // Маппинг результата Tuple в EventFullDto
        List<EventFullDto> resultList = tuples.stream()
                .map(tuple -> {
                    String state = tuple.get("state", State.class) != null ? tuple.get("state", State.class).name() : "";
                    return new EventFullDto(
                            tuple.get("annotation", String.class) != null ? tuple.get("annotation", String.class) : "",
                            new CategoryDto(
                                    tuple.get("categoryId", Long.class),
                                    tuple.get("categoryName", String.class)
                            ),
                            0, // Если confirmedRequests нужно, получаем его позже
                            tuple.get("createdOn", LocalDateTime.class),
                            tuple.get("description", String.class) != null ? tuple.get("description", String.class) : "",
                            tuple.get("eventDate", LocalDateTime.class),
                            tuple.get("eventId", Long.class),
                            new UserShortDto(
                                    tuple.get("initiatorId", Long.class),
                                    tuple.get("initiatorName", String.class)
                            ),
                            new Location(
                                    tuple.get("lat", Float.class),
                                    tuple.get("lon", Float.class)
                            ),
                            tuple.get("paid", Boolean.class),
                            tuple.get("participantLimit", Integer.class),
                            tuple.get("publishedOn", LocalDateTime.class),
                            tuple.get("requestModeration", Boolean.class),
                            state, // Преобразованное состояние
                            tuple.get("title", String.class)
                    );
                })
                .collect(Collectors.toList());

        // Получаем статистику просмотров для событий
        List<String> uris = resultList.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        Map<String, Long> viewsMap = getViewsForEvents(rangeStart, rangeEnd, uris);

        // Устанавливаем количество просмотров для каждого EventFullDto
        resultList.forEach(event -> {
            String uri = "/events/" + event.getId();
            event.setViews(viewsMap.getOrDefault(uri, 0L));
        });

        // Получаем количество подтвержденных запросов для каждого события
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequests(
                resultList.stream().map(EventFullDto::getId).collect(Collectors.toList())
        );

        // Устанавливаем количество подтвержденных запросов для каждого EventFullDto
        resultList.forEach(event -> event.setConfirmedRequests(confirmedRequestsMap.getOrDefault(event.getId(),
                0L).intValue()));

        return resultList;
    }

    ///TODO Ждать пока будет готова реализация с CONFIRMED request
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
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Event> eventTable = query.from(Event.class);

        Join<Event, Category> categoryJoin = eventTable.join("category");
        Join<Event, User> initiatorJoin = eventTable.join("initiator");

        // Выбираем поля с алиасами для использования в маппинге
        query.multiselect(
                eventTable.get("annotation").alias("annotation"),
                categoryJoin.get("id").alias("categoryId"),
                categoryJoin.get("name").alias("categoryName"),
                eventTable.get("eventDate").alias("eventDate"),
                eventTable.get("id").alias("eventId"),
                initiatorJoin.get("id").alias("initiatorId"),
                initiatorJoin.get("name").alias("initiatorName"),
                eventTable.get("paid").alias("paid"),
                eventTable.get("title").alias("title")
        );

        // Создаем предикаты для условий запроса
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

        // Сортировка по полю eventDate
        if (sort != null && sort.equals(SortType.EVENT_DATE)) {
            query.orderBy(cb.asc(eventTable.get("eventDate")));
        }

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);

        List<Tuple> tuples = typedQuery.getResultList();

        // Маппинг результата Tuple в EventShortDto
        List<EventShortDto> resultList = tuples.stream()
                .map(tuple -> new EventShortDto(
                        tuple.get("annotation", String.class),
                        new CategoryDto(
                                tuple.get("categoryId", Long.class),
                                tuple.get("categoryName", String.class)
                        ),
                        tuple.get("eventDate", LocalDateTime.class),
                        tuple.get("eventId", Long.class),
                        new UserShortDto(
                                tuple.get("initiatorId", Long.class),
                                tuple.get("initiatorName", String.class)
                        ),
                        tuple.get("paid", Boolean.class),
                        tuple.get("title", String.class)
                ))
                .collect(Collectors.toList());

        // Получаем статистику просмотров для событий
        List<String> uris = resultList.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        Map<String, Long> viewsMap = getViewsForEvents(rangeStart, rangeEnd, uris);

        // Устанавливаем количество просмотров для каждого EventShortDto
        resultList.forEach(event -> {
            String uri = "/events/" + event.getId();
            event.setViews(viewsMap.getOrDefault(uri, 0L));
        });

        // Сортировка по количеству просмотров, если указано
        if (sort != null && sort.equals(SortType.VIEWS)) {
            resultList.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }

        return resultList;
    }


    private Map<String, Long> getViewsForEvents(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<String> uris) {
        // Форматируем временной диапазон в строку
        String start = rangeStart != null ? rangeStart.toString() : LocalDateTime.now().toString();
        String end = rangeEnd != null ? rangeEnd.toString() : LocalDateTime.now().toString();

        // Запрашиваем статистику просмотров
        ViewStatsDto[] stats = statsClient.getStats(start, end, uris.toArray(new String[0]), false);

        // Преобразуем массив ViewStatsDto в Map для быстрого доступа по URI
        return Arrays.stream(stats)
                .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits, (a, b) -> b));
    }

    private Map<Long, Long> getConfirmedRequests(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> countQuery = cb.createTupleQuery();
        Root<ParticipationRequest> requestTable = countQuery.from(ParticipationRequest.class);

        countQuery.multiselect(
                requestTable.get("event").get("id"),
                cb.count(requestTable).alias("confirmedCount")
        );
        countQuery.where(
                requestTable.get("event").get("id").in(eventIds),
                cb.equal(requestTable.get("status"), StatusRequest.CONFIRMED)
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