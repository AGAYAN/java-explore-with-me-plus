package ru.practicum.event.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.*;

public interface EventService {

  EventFullDto addEvent(Long initiatorId, NewEventDto eventDto);

  List<EventShortDto> getEvents(Long initiatorId, Integer from, Integer size);

  EventFullDto getEvent(Long initiatorId, Long eventId);

  //TODO rename it to the getEvent(GetEventAdminRequest param)
  List<EventFullDto> adminGetEvent(GetEventAdminRequest param);

  //TODO  rename it to updateEvent(long eventId, UpdateEventAdminRequest param)
  EventFullDto adminPatchEvent(long eventId, UpdateEventAdminRequest param);

  EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto);

  List<EventShortDto> getEvents(GetEventPublicParam param, HttpServletRequest request);

  EventFullDto getEventsById(Long eventId, HttpServletRequest request);

}
