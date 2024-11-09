package ru.practicum.compilation.repository;

import java.util.List;
import ru.practicum.compilation.dto.CompilationParam;
import ru.practicum.compilation.model.Compilation;

public interface CompilationQueryRepository {

  List<Compilation> findAllBy(CompilationParam searchParam);

}
