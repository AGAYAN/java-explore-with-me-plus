package ru.practicum.event.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.GetEventAdminRequest;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.enums.State;
import ru.practicum.event.enums.StateAction;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final StatsClient statsClient;

  /**
   * Adds a new event initiated by a user.
   */
  @Override
  public EventFullDto addEvent(final Long initiatorId, final NewEventDto eventDto) {
    log.debug("Persisting a new event with data: {} posted by user with ID={}.", eventDto,
        initiatorId);

    final User initiator = fetchUser(initiatorId);
    final Category category = fetchCategory(eventDto.getCategory());
    final Event eventToSave = EventMapper.toEvent(eventDto, initiator, category);

    return EventMapper.toFullDto(eventRepository.save(eventToSave));
  }

  /**
   * Retries all events created by current user.
   */
  @Override
  public List<EventShortDto> getEvents(final Long initiatorId, final Integer from,
                                       final Integer size) {
    log.debug("Fetching events posted by user with ID={}.", initiatorId);
    validateUser(initiatorId);
    final PageRequest page = PageRequest.of(from / size, size);
    final List<Event> events = eventRepository.findAllByInitiatorId(initiatorId, page).getContent();
    setConfirmedRequests(events);
    setViews(events);
    return EventMapper.toShortDto(events);
  }

  /**
   * Retrieves complete data about specific event created by current user.
   */
  @Override
  public EventFullDto getEvent(final Long initiatorId, final Long eventId) {
    log.debug("Fetching event ID={}, posted by user with ID={}.", eventId, initiatorId);
    validateUser(initiatorId);
    final Event event = eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
        .orElseThrow(() -> {
          log.warn("Event ID={} with initiator ID={} not found.", eventId, initiatorId);
          return new NotFoundException("Event with was not found.");
        });
    setConfirmedRequests(List.of(event));
    setViews(List.of(event));
    return EventMapper.toFullDto(event);
  }

  //  TODO to decompose this for better readability
  private void setViews(final List<Event> events) {
    log.debug("Setting views to the events list.");
    if (events == null || events.isEmpty()) {
      log.debug("Events list is empty.");
      return;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    final String start = events.stream()
        .min(Comparator.comparing(Event::getCreatedOn))
        .map(event -> event.getCreatedOn().format(formatter))
        .orElse(LocalDateTime.now().format(formatter));

    final String end = LocalDateTime.now().format(formatter);

    final String[] uris = events.stream()
        .map(e -> buildEventUri(e.getId()))
        .toArray(String[]::new);

    log.debug("Calling StatsClient with parameters: start={}, end={}, uris={}, unique={}.",
        start, end, Arrays.toString(uris), true);
    final Map<String, Long> views = Arrays.stream(statsClient.getStats(start, end, uris, true))
        .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));

    events.forEach(event ->
        event.setViews(views.getOrDefault(buildEventUri(event.getId()), 0L)));
    log.debug("Views has set successfully.");
  }

  private String buildEventUri(final Long eventId) {
    return String.format("/events/%d", eventId);
  }

  //TODO waiting for request api is ready to fix following
  private void setConfirmedRequests(final List<Event> events) {
    log.debug("Setting Confirmed requests to the events list.");
    if (events.isEmpty()) {
      log.debug("Events list is empty.");
      return;
    }
    //extract List of event IDs
    final List<Long> eventIds = events.stream().map(Event::getId).toList();
    // get all request data with conditions:
    // 1. eventId in (eventIds),
    // 2. status == CONFIRMED
    // group them by eventId and save each value eventId in the List<Ivents>
    //result write into Map<eventId,List<requests data>>:
//    final Map<Long,ParticipationRequest> confirmedRequests =
//        requestRepository.findAllByEventIdInAndStatus(eventIds, RequestStatus.CONFIRMED)
//            .stream()
//            .collect(Collectors.groupingBy(participantRequest -> participantRequest.getEvent().getId()));
//    events.forEach(event -> event.getConfirmedRequests(confirmedRequests.get(event.getId()).size));
    log.debug("Confirmed requests has set successfully.");
  }

  private void validateUser(final Long userId) {
    if (!userRepository.existsById(userId)) {
      log.warn("User ID={} not found", userId);
      throw new NotFoundException("User not found.");
    }
    log.debug("Validating userID={} is successful.", userId);
  }

  /**
   * Retrieves All existed in DB events (performed by ADMIN).
   */
  @Override
  public List<EventFullDto> adminGetEvent(GetEventAdminRequest param) {
    log.info("Received request GET /admin/events with param {}", param);
    Pageable pageable = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
    Page<EventFullDto> eventsPage = eventRepository.adminFindEvents(
            param.getUsers(),
            param.getStates(),
            param.getCategories(),
            param.getRangeStart(),
            param.getRangeEnd(),
            pageable
    );
    return eventsPage.getContent();
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

    if (param.getStateAction().equals(StateAction.PUBLISH_EVENT.name()) && !event.getState().equals(State.PENDING)) {
      throw new ConflictException("Cannot publish the event because it's not in the right state: "
              + event.getState());
    }

    if (param.getStateAction().equals(StateAction.REJECT_EVENT.name()) && event.getState().equals(State.PUBLISHED)) {
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
    if (views != null && views.length > 0) {
      event.setViews(views[0].getHits());
    } else {
      event.setViews(0L);
    }

    return EventMapper.toFullDto(eventRepository.save(event));
  }

  private User fetchUser(final Long userId) {
    log.debug("Fetching User ID={}.", userId);
    return userRepository.findById(userId).orElseThrow(() -> {
      log.warn("User ID={} not found in DB.", userId);
      return new NotFoundException("User not found.");
    });
  }

  private Category fetchCategory(final Long categoryId) {
    log.debug("Fetching Category ID={}.", categoryId);
    return categoryRepository.findById(categoryId).orElseThrow(() -> {
      log.warn("Category ID={} not found in DB.", categoryId);
      return new NotFoundException("Category not found.");
    });
  }

}
