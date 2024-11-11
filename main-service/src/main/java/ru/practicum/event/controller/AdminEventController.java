package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.GetEventAdminRequest;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@RestController
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> adminGetEvent(@RequestParam(value = "users", required = false) List<Long> users,
                                            @RequestParam(value = "states", required = false) List<String> states,
                                            @RequestParam(value = "categories", required = false) List<Long> categories,
                                            @RequestParam(value = "rangeStart", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(value = "rangeEnd", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        GetEventAdminRequest param = new GetEventAdminRequest()
            .setUsers(users)
            .setStates(states)
            .setCategories(categories)
            .setRangeStart(rangeStart)
            .setRangeEnd(rangeEnd)
            .setFrom(from)
            .setSize(size);
        log.info("Received request GET /admin/events with param {}", param);
        return eventService.getEvent(param);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto adminPatchEvent(@PathVariable int eventId, @RequestBody @Validated UpdateEventAdminRequest param) {
        log.info("Received request PATCH /admin/events/{} with param {}", eventId, param);
        return eventService.updateEvent(eventId, param);
    }
}
