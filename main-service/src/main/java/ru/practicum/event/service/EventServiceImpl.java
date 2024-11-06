package ru.practicum.event.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.GetEventAdminRequest;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.enums.State;
import ru.practicum.event.enums.StateAction;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventQueryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventQueryRepository eventRepositoryImpl;
  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final LocationRepository locationRepository;
  private final StatsClient statsClient;

  @Override
  public EventFullDto addEvent(final Long initiatorId, final NewEventDto eventDto) {
    log.debug("Persisting a new event with data: {} posted by user with ID={}.", eventDto, initiatorId);

    final User initiator = fetchUser(initiatorId);
    final Category category = fetchCategory(eventDto.getCategory());
    final Location location = saveLocation(eventDto.getLocation());

    final Event eventToSave = EventMapper.toEvent(eventDto,initiator,category);

    return EventMapper.toFullDto(eventRepository.save(eventToSave));
  }

  @Override
  public List<EventFullDto> adminGetEvent(GetEventAdminRequest param) {
    log.info("Received request GET /admin/events with param {}", param);
    Pageable pageable = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
      return eventRepositoryImpl.adminFindEvents(
            param.getUsers(),
            param.getStates(),
            param.getCategories(),
            param.getRangeStart(),
            param.getRangeEnd(),
            param.getFrom(),
            param.getSize()
    );
  }

  ///TODO жду реализации GET events/ api с досутпом PUBLIC
  @Override
  public EventFullDto adminPatchEvent(long eventId, UpdateEventAdminRequest param) {
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

    if (param.getEventDate() != null &&
            param.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
      throw new ConflictException("Cannot update event date to a time less than one hour from now");
    }

    if (param.getStateAction().equals(StateAction.PUBLISH_EVENT) && !event.getState().equals(State.PENDING)) {
      throw new ConflictException("Cannot publish the event because it's not in the right state: "
              + event.getState());
    }

    if (param.getStateAction().equals(StateAction.REJECT_EVENT) && event.getState().equals(State.PUBLISHED)) {
      throw new ConflictException("Cannot reject the event because it is already published");
    }

    if (param.getAnnotation() != null) {
      event.setAnnotation(param.getAnnotation());
    }
    if (param.getCategory() != null) {
      Category category = categoryRepository.findById(param.getCategory())
              .orElseThrow(() -> new NotFoundException("Category not found"));
      event.setCategory(category);
    }
    if (param.getDescription() != null) {
      event.setDescription(param.getDescription());
    }
    if (param.getEventDate() != null) {
      event.setEventDate(param.getEventDate());
    }
    if (param.getLocation() != null) {
      Location location = new Location();
      location.setLat(param.getLocation().getLat());
      location.setLon(param.getLocation().getLon());
      locationRepository.save(location);
      event.setLocation(location);
    }
    if (param.getPaid() != null) {
      event.setPaid(param.getPaid());
    }
    if (param.getParticipantLimit() != null) {
      event.setParticipantLimit(param.getParticipantLimit());
    }
    if (param.getRequestModeration() != null) {
      event.setRequestModeration(param.getRequestModeration());
    }
    if (param.getTitle() != null) {
      event.setTitle(param.getTitle());
    }

    String start = event.getCreatedOn().toString();
    String end = LocalDateTime.now().toString();
    log.debug("Get views for event with id={}", eventId);
    ViewStatsDto[] views = statsClient.getStats(start, end, new String[]{"/events/" + eventId}, true);
    ///TODO думаю что можно оставить как есть пока не нашёл как сделать лучше.
    if (views == null) {
      event.setViews(views[0].getHits());
    } else {
      event.setViews(0L);
    }

    return EventMapper.toFullDto(eventRepository.save(event));
  }

  private User fetchUser(final Long userId) {
    return userRepository.findById(userId).orElseThrow(() -> {
      log.warn("User ID={} not found in DB.", userId);
      return new NotFoundException("User not found.");
    });
  }

  private Category fetchCategory(final Long categoryId) {
    return categoryRepository.findById(categoryId).orElseThrow(() -> {
      log.warn("Category ID={} not found in DB.", categoryId);
      return new NotFoundException("Category not found.");
    });
  }

  private Location saveLocation(final Location location) {
    return location.getId() == null ? locationRepository.save(location) : location;
  }

}
