package ru.practicum.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationParam;
import ru.practicum.compilation.service.CompilationService;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCompilationController {

  private final CompilationService service;

  @GetMapping
  public ResponseEntity<List<CompilationDto>> get(@RequestParam(required = false) Boolean pinned,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
    log.info(
        "Request received GET /compilations to retrieve compilation pinned = {}, from {}, size {}",
        pinned, from, size);
    final List<CompilationDto> compilations = service.get(new CompilationParam(pinned, from, size));
    log.info("Returning {} Compilations.", compilations.size());
    return ResponseEntity.status(HttpStatus.OK).body(compilations);
  }

  @GetMapping("/{compId}")
  public ResponseEntity<CompilationDto> get(@PathVariable("compId") @Positive Long compId) {
    log.info("Request received GET /compilations/{}.", compId);
    final CompilationDto compilation = service.get(compId);
    log.info("Returning Compilations with ID {}.", compilation.getId());
    return ResponseEntity.status(HttpStatus.OK).body(compilation);
  }

}
