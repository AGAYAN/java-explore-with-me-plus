package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndPointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.GetEventPublicParam;
import ru.practicum.event.enums.SortType;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(value = "text", required = false) String text,
                                         @RequestParam(value = "categories", required = false) List<Long> categories,
                                         @RequestParam(value = "paid", required = false) Boolean paid,
                                         @RequestParam(value = "rangeStart", required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(value = "rangeEnd", required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false")
                                             boolean onlyAvailable,
                                         @RequestParam(value = "sort", required = false) SortType sort,
                                         @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                         @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                         HttpServletRequest request) {
        GetEventPublicParam params = new GetEventPublicParam()
                .setText(text)
                .setCategories(categories)
                .setPaid(paid)
                .setRangeStart(rangeStart)
                .setRangeEnd(rangeEnd)
                .setOnlyAvailable(onlyAvailable)
                .setSort(sort)
                .setFrom(from)
                .setSize(size);

        log.info("Request received GET /events with params {}", params);
        List<EventShortDto> events = eventService.getEvents(params, request);
        log.info("Events received: {}", events);
        saveHitStatistic(request);
        return events;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventsById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Request received GET /events with id {}", eventId);
        EventFullDto event = eventService.getEvent(eventId);
        log.info("Event received: {}", event);
        saveHitStatistic(request);
        return event;
    }

    private void saveHitStatistic(HttpServletRequest request) {
        EndPointHitDto hitDto = new EndPointHitDto();
        hitDto.setApp("explore-with-me");
        hitDto.setUri(request.getRequestURI());
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setRequestTime(LocalDateTime.now());
        statsClient.saveHit(hitDto);
    }
}
