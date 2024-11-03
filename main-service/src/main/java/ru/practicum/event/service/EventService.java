package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.GetEventAdminRequest;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;

import java.util.List;

public interface EventService {

  EventFullDto addEvent(Long userId, NewEventDto eventDto);

  List<EventFullDto> adminGetEvent(GetEventAdminRequest param);

  EventFullDto adminPatchEvent(long eventId, UpdateEventAdminRequest param);
}
