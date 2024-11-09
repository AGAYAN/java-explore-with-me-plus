package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;

public interface CompilationService {

  CompilationDto saveCompilation(NewCompilationDto compilationDto);

}
