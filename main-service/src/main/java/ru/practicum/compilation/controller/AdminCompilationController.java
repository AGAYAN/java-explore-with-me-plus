package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.service.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationController {

  private final CompilationService service;

  @PostMapping
  public ResponseEntity<CompilationDto> saveCompilation(
      @Validated @RequestBody NewCompilationDto compilationDto) {
    log.info("Request received POST /admin/compilations to save compilation {}", compilationDto);

    final CompilationDto savedCompilation = service.saveCompilation(compilationDto);
    log.info("Compilation saved successfully with ID={}.", savedCompilation.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCompilation);
  }

}
