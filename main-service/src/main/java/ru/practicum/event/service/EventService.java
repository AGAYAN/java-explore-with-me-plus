package ru.practicum.event.service;

import java.util.List;
import java.util.Set;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.GetEventAdminRequest;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;

public interface EventService {

  EventFullDto addEvent(Long initiatorId, NewEventDto eventDto);

  List<EventShortDto> getEvents(Long initiatorId, Integer from, Integer size);

  EventFullDto getEvent(Long initiatorId, Long eventId);

  //TODO rename it to the getEvent(GetEventAdminRequest param)
  List<EventFullDto> adminGetEvent(GetEventAdminRequest param);

  //TODO  rename it to updateEvent(long eventId, UpdateEventAdminRequest param)
  EventFullDto adminPatchEvent(long eventId, UpdateEventAdminRequest param);

  EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto);

  List<ParticipationRequestDto> getRequests(Long initiatorId, Long eventId);

  EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateStatusDto);

  Set<Event> getEvents(Set<Long> events);

}
