package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.GetEventPublicParam;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestBody GetEventPublicParam param, HttpServletRequest request) {
        log.info("Request received GET /events with params {}", param);
        return eventService.getEvents(param, request);
    }

    @GetMapping("/eventId")
    public EventFullDto getEventsById(@PathVariable long eventId) {
        log.info("Request received GET /events with id {}", eventId);
        return eventService.getEventsById(eventId);
    }
}
