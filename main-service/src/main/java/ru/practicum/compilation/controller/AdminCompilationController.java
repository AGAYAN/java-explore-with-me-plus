package ru.practicum.compilation.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
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
      @Validated @RequestBody NewCompilationDto compDto) {
    log.info("Request received POST /admin/compilations to save compilation {}", compDto);

    final CompilationDto savedCompilation = service.save(compDto);
    log.info("Compilation saved successfully with ID={}.", savedCompilation.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCompilation);
  }

  @DeleteMapping("/{compId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCompilation(@PathVariable("compId") @Positive Long compId) {
    log.info("Request received DELETE /admin/compilations/{} to delete compilation.", compId);
    service.delete(compId);
    log.info("Compilation deleted successfully.");
  }

  @PatchMapping("/{compId}")
  public ResponseEntity<CompilationDto> updateCompilation(@PathVariable("compId") @Positive Long compId,
      @Validated @RequestBody UpdateCompilationRequest compDto) {
    log.info("Request received PATCH /admin/compilations/{} to update compilation with data {}.",
        compId, compDto);
    final CompilationDto updatedCompilation = service.update(compId, compDto);
    log.info("Compilation saved successfully with ID={}.", updatedCompilation.getId());
    return ResponseEntity.status(HttpStatus.OK).body(updatedCompilation);
  }

}
