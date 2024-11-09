package ru.practicum.compilation.mapper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

@UtilityClass
@Slf4j
public class CompilationMapper {

  public static Compilation toCompilation(final NewCompilationDto compilationDto,
                                          final Set<Event> events) {
    log.debug("Mapping NewCompilationDto {} to the Compilation.", compilationDto);
    Objects.requireNonNull(compilationDto);
    return new Compilation()
        .setEvents(events)
        .setPinned(compilationDto.getPinned())
        .setTitle(compilationDto.getTitle());
  }

  public static CompilationDto toCompilationDto(final Compilation compilation) {
    log.debug("Mapping Compilation {} to the CompilationDto.", compilation);
    Objects.requireNonNull(compilation);
    return new CompilationDto()
        .setId(compilation.getId())
        .setEvents(new HashSet<>(EventMapper.toShortDto(compilation.getEvents()
            .stream()
            .toList())))
        .setPinned(compilation.isPinned())
        .setTitle(compilation.getTitle());
  }
}
