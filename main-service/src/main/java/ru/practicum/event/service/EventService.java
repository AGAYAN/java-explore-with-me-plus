package ru.practicum.event.service;

import java.util.List;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.GetEventAdminRequest;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;

import java.util.List;

public interface EventService {

  EventFullDto addEvent(Long initiatorId, NewEventDto eventDto);

  List<EventShortDto> getEvents(Long initiatorId, Integer from, Integer size);

  EventFullDto getEvent(Long initiatorId, Long eventId);

//  EventFullDto addEvent(Long userId, NewEventDto eventDto);

  List<EventFullDto> adminGetEvent(GetEventAdminRequest param);

  EventFullDto adminPatchEvent(long eventId, UpdateEventAdminRequest param);
}
