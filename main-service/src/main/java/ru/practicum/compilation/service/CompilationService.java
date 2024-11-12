package ru.practicum.compilation.service;

import java.util.List;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationParam;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

public interface CompilationService {

  CompilationDto save(NewCompilationDto compilationDto);

  void delete(Long compId);

  CompilationDto update(Long compId, UpdateCompilationRequest compDto);

  List<CompilationDto> get(CompilationParam searchParam);

  CompilationDto get(Long compId);
}
