package ru.practicum.event.repository;

import ru.practicum.event.dto.EventFullDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventQueryRepository {
    public List<EventFullDto> adminFindEvents(final List<Long> users,
                                              final List<String> states,
                                              final List<Long> categories,
                                              final LocalDateTime rangeStart,
                                              final LocalDateTime rangeEnd,
                                              int from,
                                              int size);
}
