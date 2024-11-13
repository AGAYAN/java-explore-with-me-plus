package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Data;

/**
 * Is used in the ADMIN API - as REQUEST body
 * <p>
 *   POST /admin/compilations
 */
@Data
public class NewCompilationDto {

  private Set<Long> events;

  private Boolean pinned = false;

  @NotBlank(message = "The title is required and cannot be blank.")
  @Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
  String title;

}
