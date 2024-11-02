package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
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
  private final LocationRepository locationRepository;

  @Override
  public EventFullDto addEvent(final Long initiatorId, final NewEventDto eventDto) {
    log.debug("Persisting a new event with data: {} posted by user with ID={}.", eventDto, initiatorId);

    final User initiator = fetchUser(initiatorId);
    final Category category = fetchCategory(eventDto.getCategory());
    final Location location = saveLocation(eventDto.getLocation());

    final Event eventToSave = EventMapper.toEvent(eventDto,initiator,category);

    return EventMapper.toFullDto(eventRepository.save(eventToSave));
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
