package ru.practicum.event.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.service.EventService;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {

  private final EventService eventService;

  @PostMapping
  public ResponseEntity<EventFullDto> addEvent(
      @PathVariable("userId") @NotNull @Positive Long userId,
      @Validated @RequestBody NewEventDto eventDto) {
    log.info("Received request POST /users/{}/events to add event {}", userId, eventDto);

    final EventFullDto eventSaved = eventService.addEvent(userId, eventDto);
    log.info("Event added successfully with ID={}.", eventSaved.getId());

    return ResponseEntity.status(HttpStatus.CREATED).body(eventSaved);
  }

}
