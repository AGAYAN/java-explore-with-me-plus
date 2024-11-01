package ru.practicum.event.mapper;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.user.mappers.UserMapper;
import ru.practicum.user.model.User;

@UtilityClass
@Slf4j
public class EventMapper {

  public Event toEvent(final NewEventDto eventDto, final User initiator, final Category category) {
    log.debug("Mapping NewEventDto {} to the Event.", eventDto);
    Objects.requireNonNull(eventDto);
    Objects.requireNonNull(initiator);
    return new Event()
        .setAnnotation(eventDto.getAnnotation())
        .setCategory(category)
        .setDescription(eventDto.getDescription())
        .setEventDate(eventDto.getEventDate())
        .setLocation(eventDto.getLocation())
        .setPaid(eventDto.getPaid())
        .setCreatedOn(LocalDateTime.now())
        .setInitiator(initiator)
        .setParticipantLimit(eventDto.getParticipantLimit())
        .setTitle(eventDto.getTitle())
        .setRequestModeration(eventDto.getRequestModeration())
        .setState(State.PENDING);


  }

  public static EventFullDto toFullDto(final Event event) {
    log.debug("Mapping Event {} to the EventFullDto.", event);
    Objects.requireNonNull(event);
    return new EventFullDto()
        .setId(event.getId())
        .setAnnotation(event.getAnnotation())
        .setCategory(CategoryMapper.toCategoryDto(event.getCategory()))
        .setConfirmedRequests(event.getConfirmedRequests())
        .setCreatedOn(event.getCreatedOn())
        .setDescription(event.getDescription())
        .setEventDate(event.getEventDate())
        .setInitiator(UserMapper.mapToUserShortDto(event.getInitiator()))
        .setLocation(event.getLocation())
        .setPaid(event.getPaid())
        .setParticipantLimit(event.getParticipantLimit())
        .setPublishedOn(event.getPublishedOn())
        .setRequestModeration(event.getRequestModeration())
        .setState(event.getState().name())
        .setTitle(event.getTitle())
        .setViews(event.getViews());

  }
}
