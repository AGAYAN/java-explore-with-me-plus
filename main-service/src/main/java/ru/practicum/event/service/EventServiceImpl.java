package ru.practicum.event.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.GetEventAdminRequest;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.enums.State;
import ru.practicum.event.enums.StateAction;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final StatsClient statsClient;

  private final UserService userService;
  private final CategoryService categoryService;

  /**
   * Adds a new event initiated by a user.
   */
  @Override
  public EventFullDto addEvent(final Long initiatorId, final NewEventDto eventDto) {
    log.debug("Persisting a new event with data: {} posted by user with ID={}.", eventDto,
        initiatorId);

    final UserDto initiator = userService.getUser(initiatorId);
    final CategoryDto category = categoryService.getCategoryById(eventDto.getCategory());
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
    final Event event = getEnrichedEvent(initiatorId, eventId);
    return EventMapper.toFullDto(event);
  }

  /**
   * Edit an event added by the current user.
   */
  @Override
  public EventFullDto updateEvent(final Long userId, final Long eventId, final UpdateEventUserRequest eventDto) {
    log.debug("Updating event ID={}, posted by user with ID={} with data {}.",
        eventId, userId,eventDto);
    final Event eventToUpdate = getEnrichedEvent(userId, eventId);
    validateEventUpdatable(eventToUpdate);
    patchEventFields(eventToUpdate, eventDto);
    eventRepository.save(eventToUpdate);
    return EventMapper.toFullDto(eventToUpdate);
  }

  /**
   * Retrieves All existed in DB events (performed by ADMIN).
   */
  //TODO rename it to the getEvent(GetEventAdminRequest param)
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

  /**
   * Updates the specified event with the provided data.(Performed by ADMIN)
   */
  ///TODO жду реализации GET events/ api с досутпом PUBLIC
  //TODO  rename it to updateEvent(long eventId, UpdateEventAdminRequest param)
  @Override
  public EventFullDto adminPatchEvent(long eventId, UpdateEventAdminRequest param) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

    if (param.getEventDate() != null &&
        param.getEventDate().isBefore(LocalDateTime.now().plusHours(1))
        || event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
      throw new ConflictException("Cannot update event date to a time less than one hour from now");
    }

    if (param.getStateAction() != null) {
      if (param.getStateAction().equals(StateAction.PUBLISH_EVENT.name()) && !event.getState()
          .equals(State.PENDING)) {
        throw new ConflictException("Cannot publish the event because it's not in the right state: "
            + event.getState());
      }

      if (param.getStateAction().equals(StateAction.REJECT_EVENT.name()) && event.getState()
          .equals(State.PUBLISHED)) {
        throw new ConflictException("Cannot reject the event because it is already published");
      }
      event.setState(StateAction.fromString(param.getStateAction()).getState());
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
      event.setLocation(param.getLocation());
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

//    String start = event.getCreatedOn().toString();
//    String end = LocalDateTime.now().toString();
//    log.debug("Get views for event with id={}", eventId);
//    ViewStatsDto[] views = statsClient.getStats(start, end, new String[]{"/events/" + eventId},
//        true);
//    ///TODO думаю что можно оставить как есть пока не нашёл как сделать лучше.
//    if (views != null && views.length > 0) {
//      event.setViews(views[0].getHits());
//    } else {
//      event.setViews(0L);
//    }
    setViews(List.of(event));
    setConfirmedRequests(List.of(event));

    return EventMapper.toFullDto(eventRepository.save(event));
  }

  private Event getEnrichedEvent(final Long initiatorId, final Long eventId) {
    log.debug("Fetching Event ID={} created bu user ID={}.",eventId, initiatorId);
    final Event event = eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
        .orElseThrow(() -> {
          log.warn("Event ID={} with initiator ID={} not found.", eventId, initiatorId);
          return new NotFoundException("Event with was not found.");
        });
    setConfirmedRequests(List.of(event));
    setViews(List.of(event));
    return event;
  }

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
    //And result write into Map<eventId,List<requests data>>:
//    final Map<Long,ParticipationRequest> confirmedRequests =
//        requestRepository.findAllByEventIdInAndStatus(eventIds, RequestStatus.CONFIRMED)
//            .stream()
//            .collect(Collectors.groupingBy(participantRequest -> participantRequest.getEvent().getId()));
//    events.forEach(event -> event.getConfirmedRequests(confirmedRequests.get(event.getId()).size));
    log.debug("Confirmed requests has set successfully.");
  }

  private void patchEventFields(final Event target, final UpdateEventUserRequest dataSource) {
    log.debug("Apply the patch at Event fields.");
    Optional.ofNullable(dataSource.getAnnotation()).ifPresent(target::setAnnotation);
    Optional.ofNullable(dataSource.getDescription()).ifPresent(target::setDescription);
    Optional.ofNullable(dataSource.getEventDate()).ifPresent(target::setEventDate);
    Optional.ofNullable(dataSource.getLocation()).ifPresent(target::setLocation);
    Optional.ofNullable(dataSource.getPaid()).ifPresent(target::setPaid);
    Optional.ofNullable(dataSource.getParticipantLimit()).ifPresent(target::setParticipantLimit);
    Optional.ofNullable(dataSource.getRequestModeration()).ifPresent(target::setRequestModeration);
    Optional.ofNullable(dataSource.getTitle()).ifPresent(target::setTitle);

    Optional.ofNullable(dataSource.getCategory()).ifPresent(categoryId ->
        target.setCategory(CategoryMapper.toCategory(categoryService.getCategoryById(categoryId))));

    Optional.ofNullable(dataSource.getStateAction()).ifPresent(stateAction -> {
      target.setState(StateAction.fromString(stateAction).getState());

    });
  }

  private void validateEventUpdatable(final Event event) {
    log.debug("Validate event date at least is two hours ahead and it is not published.");
    validateEventDate(event.getEventDate());
    validateEventState(event);
  }

  private void validateEventState(Event event) {
    if (State.PUBLISHED.equals(event.getState())) {
      throw new ConflictException("Only pending or canceled events can be changed");
    }
  }

  private void validateEventDate(final LocalDateTime eventDate) {
    log.debug("Validate event date at least is two hours ahead..");
    if (!eventDate.isAfter(LocalDateTime.now().plusHours(2))) {
      throw new ConflictException("The event date must be at least two hours in the future.");
    }
  }

  private void validateUser(final Long userId) {
    userService.validateUserExist(userId);
  }

}
