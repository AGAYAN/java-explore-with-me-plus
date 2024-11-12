package ru.practicum.event.service;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.GetEventAdminRequest;
import ru.practicum.event.dto.GetEventPublicParam;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.enums.State;
import ru.practicum.event.enums.StateAction;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.StatusRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final UserService userService;
  private final CategoryService categoryService;
  private final RequestRepository requestRepository;
  private final StatsClient statsClient;

  /**
   * Saves a new event data initiated by a current user.
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
   * Updates specified event with the provided data (Performed by ADMIN).
   */
  @Override
  public EventFullDto updateEvent(final long eventId, final UpdateEventAdminRequest param) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found."));

    validateEventUpdatable(event,param);

    if (param.getStateAction() != null) {
      State newState = StateAction.fromString(param.getStateAction()).getState();
      event.setState(newState);
      if (State.PUBLISHED.equals(newState)) {
        event.setPublishedOn(LocalDateTime.now());
      }
    }

    if (param.getAnnotation() != null) {
      event.setAnnotation(param.getAnnotation());
    }

    if (param.getCategory() != null) {
      Category category = CategoryMapper.toCategory(categoryService.getCategoryById(param.getCategory()));
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

    setViews(List.of(event));
    setConfirmedRequests(List.of(event));

    return EventMapper.toFullDto(eventRepository.save(event));
  }

  /**
   * Updates the specified event by the current user (who is the initiator of the event).
   */
  @Override
  public EventFullDto updateEvent(final Long userId, final Long eventId,
                                  final UpdateEventUserRequest eventDto) {
    log.debug("Updating event ID={}, posted by user with ID={} with data {}.",
        eventId, userId, eventDto);
    final Event eventToUpdate = getEnrichedEvent(userId, eventId);
    validateEventUpdatable(eventToUpdate, null);
    patchEventFields(eventToUpdate, eventDto);
    eventRepository.save(eventToUpdate);
    return EventMapper.toFullDto(eventToUpdate);
  }

  /**
   * Retrieves complete data about specific event created by current user.
   */
  @Transactional(readOnly = true)
  @Override
  public EventFullDto getEvent(final Long initiatorId, final Long eventId) {
    log.debug("Fetching event ID={}, posted by user with ID={}.", eventId, initiatorId);
    final Event event = getEnrichedEvent(initiatorId, eventId);
    return EventMapper.toFullDto(event);
  }

  /**
   * Retrieves detailed information about a published event by its ID.
   */
  @Transactional(readOnly = true)
  @Override
  public EventFullDto getEvent(final Long eventId) {
    log.debug("Fetching event ID={}.", eventId);
    Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
        .orElseThrow(() -> new NotFoundException(
            "Event with id " + eventId + " not found or not published"));

    setConfirmedRequests(List.of(event));
    setViews(List.of(event));
    return EventMapper.toFullDto(event);
  }

  /**
   * Retrieves all existed in DB events that match the given conditions in the GetEventAdminRequest(performed by ADMIN).
   */
  @Transactional(readOnly = true)
  @Override
  public List<EventFullDto> getEvents(GetEventAdminRequest param) {
    log.info("Received request GET /admin/events with param {}", param);
    return eventRepository.adminFindEvents(
        param.getUsers(),
        param.getStates(),
        param.getCategories(),
        param.getRangeStart(),
        param.getRangeEnd(),
        param.getFrom(),
        param.getSize()
    );
  }

  /**
   * Retries all events created by current user.
   */
  @Transactional(readOnly = true)
  @Override
  public List<EventShortDto> getEvents(final Long initiatorId, final Integer from,
                                       final Integer size) {
    log.debug("Fetching events posted by user with ID={}.", initiatorId);
    validateUserExist(initiatorId);
    final PageRequest page = PageRequest.of(from / size, size);
    final List<Event> events = eventRepository.findAllByInitiatorId(initiatorId, page).getContent();
    setConfirmedRequests(events);
    setViews(events);
    return EventMapper.toShortDto(events);
  }

  /**
   * Retrieving published events with filtering options.
   */
  @Transactional(readOnly = true)
  @Override
  public List<EventShortDto> getEvents(GetEventPublicParam param, HttpServletRequest request) {
    log.debug("Fetching events with params {}", param);
    if (param.getRangeStart() != null && param.getRangeEnd() != null &&
        param.getRangeStart().isAfter(param.getRangeEnd())) {
      throw new BadRequestException("Start date should be before end date");
    }
    return eventRepository.publicGetEvents(
        param.getText(),
        param.getCategories(),
        param.getPaid(),
        param.getRangeStart(),
        param.getRangeEnd(),
        param.getOnlyAvailable(),
        param.getSort(),
        param.getFrom(),
        param.getSize());
  }

  /**
   *  Retrieves a set of events based on the provided event IDs.
   */
  @Transactional(readOnly = true)
  @Override
  public Set<Event> getEvents(final Set<Long> eventIds) {
    log.debug("Retrieving set of events by theirs IDs: {}.", eventIds);
    Objects.requireNonNull(eventIds);
    return eventIds.isEmpty() ? Set.of() : eventRepository.findAllDistinctByIdIn(eventIds);
  }


  /**
   * Retrieves information about participation requests for the current user's event.
   */
  @Transactional(readOnly = true)
  @Override
  public List<ParticipationRequestDto> getRequests(final Long initiatorId, final Long eventId) {
    log.debug("Retrieving event participants for event ID={}, posted by user ID={}.", eventId,
        initiatorId);
    validateUserExist(initiatorId, eventId);
    return RequestMapper.mapToDto(
        requestRepository.findAllByEventIdAndEventInitiatorId(eventId, initiatorId));
  }

  /**
   * Updates the participation request statuses for the specified event of the current user. The
   * statuses can be changed to either {@code CONFIRMED} or {@code REJECTED}.
   */
  @Override
  public EventRequestStatusUpdateResult updateRequestsStatus(
      final Long initiatorId,
      final Long eventId,
      final EventRequestStatusUpdateRequest updateStatusDto) {
    log.debug(
        "Updating participation requests {} for the event {} created by user {} with statusRequest {}",
        updateStatusDto.getRequestIds(), eventId, initiatorId, updateStatusDto.getStatus());

    validateUserExist(initiatorId);

    final Event event = fetchEvent(eventId, initiatorId);
    final StatusRequest newStatus = updateStatusDto.getStatus();
    final Boolean isModerated = event.getRequestModeration();
    final Integer participantLimit = event.getParticipantLimit();
    final Integer confirmed = event.getConfirmedRequests();

    if (!isModerated || participantLimit == 0) {
      return autoConfirmRequests(updateStatusDto.getRequestIds(), eventId);
    }

    final List<ParticipationRequest> requestsToUpdate = getPendingRequests(
        updateStatusDto.getRequestIds(), eventId);

    int availableSlots = participantLimit - confirmed;
    if (availableSlots <= 0) {
      log.warn(
          "Participant limit for the event {} has been reached: limit={}, confirmed requests={}.",
          eventId, event.getParticipantLimit(), event.getConfirmedRequests());
      throw new ConflictException("Participant limit for this event has been reached.");
    }
    return processRequestsWithLimit(requestsToUpdate, newStatus, availableSlots);
  }

  /**
   * Gets specified Event created by User with given ID, with confirmedRequests and views data set.
   */
  private Event getEnrichedEvent(final Long initiatorId, final Long eventId) {
    final Event event = fetchEvent(eventId, initiatorId);
    setViews(List.of(event));
    return event;
  }

  private Event fetchEvent(final Long eventId, final Long initiatorId) {
    log.debug("Fetching An event ID{} with initiator ID {}.", eventId, initiatorId);
    return eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
        .map(result -> result.getEvent().setConfirmedRequests(result.getConfirmedRequests()))
        .orElseThrow(
            () -> {
              log.warn("Event ID={} with initiator ID={} not found.", eventId, initiatorId);
              return new NotFoundException("Event was not found.");
            });
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

  private void setConfirmedRequests(final List<Event> events) {
    log.debug("Setting Confirmed requests to the events list {}.", events);
    if (events.isEmpty()) {
      log.debug("Events list is empty.");
      return;
    }
    final List<Long> eventIds = events.stream().map(Event::getId).toList();
    final Map<Long, List<ParticipationRequest>> confirmedRequests =
        requestRepository.findAllByEventIdInAndStatus(eventIds, StatusRequest.CONFIRMED)
            .stream()
            .collect(Collectors.groupingBy(
                participantRequest -> participantRequest.getEvent().getId()));

    events.forEach(event ->
        event.setConfirmedRequests(
            confirmedRequests.getOrDefault(event.getId(), List.of()).size()));
    log.debug("Confirmed requests has set successfully to the events with IDs {}.", eventIds);
  }

  private void patchEventFields(final Event target, final UpdateEventUserRequest dataSource) {
    log.debug("Apply the patch on Event fields.");
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

    Optional.ofNullable(dataSource.getStateAction())
        .ifPresent(stateAction ->
            target.setState(StateAction.fromString(stateAction).getState()));
  }

  private void validateEventUpdatable(final Event event, final @Nullable UpdateEventAdminRequest param) {
    log.debug("Validate event date is in the future and has right state.");
    if (param != null) {
      validateEventDate(event.getEventDate(), 1);
      validateEventState(event,param);
    } else {
      validateEventDate(event.getEventDate(), 2);
      validateEventState(event);
    }
  }

  private void validateEventState(final Event event) {
    if (State.PUBLISHED.equals(event.getState())) {
      throw new ConflictException("Only pending or canceled events can be changed");
    }
  }

  private void validateEventState(final Event event, final UpdateEventAdminRequest param) {
    if (param.getStateAction() != null) {
      if (param.getStateAction().equals(StateAction.PUBLISH_EVENT.name()) && !event.getState()
          .equals(State.PENDING)) {
        throw new ConflictException("Cannot publish the event because it's not in the right state: "
            + event.getState());
      }
      if (param.getStateAction().equals(StateAction.REJECT_EVENT.name()) && event.getState()
          .equals(State.PUBLISHED)) {
        throw new ConflictException("Cannot reject the event because it is already published.");
      }
    }
  }

  private void validateEventDate(final LocalDateTime eventDate, final int minimumTimeGap) {
    log.debug("Validate event date at least is two hours ahead..");
    if (!eventDate.isAfter(LocalDateTime.now().plusHours(minimumTimeGap))) {
      throw new ConflictException("The event date must be at least two hours in the future.");
    }
  }

  private void validateUserExist(final Long userId, final Long eventId) {
    validateUserExist(userId);
    if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
      log.warn("Event ID={} with intiator ID={} not exists.", userId, eventId);
      throw new NotFoundException("Event with current initiator not found.");
    }
  }

  private void validateUserExist(final Long userId) {
    userService.validateUserExist(userId);
  }

  private EventRequestStatusUpdateResult autoConfirmRequests(final List<Long> requestIds,
                                                             final Long eventId) {
    log.debug("Confirming All requests.");
    final List<ParticipationRequest> requestsToUpdate = getPendingRequests(requestIds, eventId);
    requestsToUpdate.forEach(request -> request.setStatus(StatusRequest.CONFIRMED));
    requestRepository.saveAll(requestsToUpdate);

    return EventMapper.toEventRequestStatusUpdateResult(requestsToUpdate, Collections.emptyList());
  }

  private List<ParticipationRequest> getPendingRequests(final List<Long> requestIds,
                                                        final Long eventId) {
    log.debug("Fetching participation requests with IDs:{} and PENDING status.", requestIds);
    final List<ParticipationRequest> requests = requestRepository.findAllByIdInAndEventIdAndStatus(
        requestIds, eventId, StatusRequest.PENDING);
    if (requestIds.size() > requests.size()) {
      log.warn("StatusRequest should be PENDING for all requests to be updated.");
      throw new ConflictException(
          "StatusRequest should be PENDING for all requests to be updated.");
    }
    return requests;
  }


  private EventRequestStatusUpdateResult processRequestsWithLimit(
      final List<ParticipationRequest> requestsToUpdate,
      final StatusRequest newStatus, final Integer availableSlots) {
    log.debug("Processing requests {} to update status with {}. Available slots ={}",
        requestsToUpdate, newStatus, availableSlots);
    final List<ParticipationRequest> confirmedRequests = new ArrayList<>();
    final List<ParticipationRequest> rejectedRequests = new ArrayList<>();
    int available = availableSlots;

    if (newStatus.equals(StatusRequest.REJECTED)) {
      requestsToUpdate.forEach(r -> r.setStatus(newStatus));
      rejectedRequests.addAll(requestRepository.saveAll(requestsToUpdate));
    } else {
      for (ParticipationRequest request : requestsToUpdate) {
        if (available > 0) {
          request.setStatus(StatusRequest.CONFIRMED);
          confirmedRequests.add(request);
          available--;
        } else {
          request.setStatus(StatusRequest.REJECTED);
          rejectedRequests.add(request);
        }
      }
    }
    requestRepository.saveAll(confirmedRequests);
    requestRepository.saveAll(rejectedRequests);
    return EventMapper.toEventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
  }
}
