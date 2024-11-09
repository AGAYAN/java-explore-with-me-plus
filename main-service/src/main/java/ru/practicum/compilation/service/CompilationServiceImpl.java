package ru.practicum.compilation.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

  private final CompilationRepository compilationRepository;
  private final EventService eventService;

  /**
   * Saves new compilation; may contain NO events.
   */
  @Override
  public CompilationDto saveCompilation(final NewCompilationDto compilationDto) {
    log.debug("Saving new compilation with data {}.", compilationDto);
    final Set<Event> events = compilationDto.getEvents() == null || compilationDto.getEvents().isEmpty()
        ? Set.of()
        : eventService.getEvents(compilationDto.getEvents());
    final Compilation toSave = CompilationMapper.toCompilation(compilationDto, events);
    try {
      final Compilation savedCompilation = compilationRepository.save(toSave);
      return CompilationMapper.toCompilationDto(savedCompilation);
    } catch (ConstraintViolationException exception) {
      log.warn("Could not execute statement. Title should be unique.");
      throw new ConflictException("Could not execute statement. Title should be unique.");
    }
  }
}
