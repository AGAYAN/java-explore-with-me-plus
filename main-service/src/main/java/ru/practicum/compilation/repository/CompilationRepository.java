package ru.practicum.compilation.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long>,
    CompilationQueryRepository {

  @EntityGraph(attributePaths = {"events", "events.category", "events.initiator"})
  @Query("""
      SELECT c
      FROM Compilation c
      WHERE c.id = :id
      """)
  Optional<Compilation> findByIdEnriched(@Param("id") Long compId);
}
