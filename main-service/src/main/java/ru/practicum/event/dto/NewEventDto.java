package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import ru.practicum.event.model.Location;
import ru.practicum.validation.MinimumHoursFromNow;

/**
 * Used in the PRIVATE API - as REQUEST body
 * <p> POST /users/{userId}/events
 */
@Data
public class NewEventDto {

  @NotBlank(message = "Annotation is required and cannot be blank.")
  @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters.")
  private String annotation;

  @NotNull(message = "Category is required.")
  @Positive(message = "Category ID must be a positive number.")
  private Long category;

  @NotBlank(message = "Description is required and cannot be blank.")
  @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters.")
  private String description;

  @MinimumHoursFromNow(hoursInFuture = 2, message = "Event date must be at least two hours in the future.")
  @NotNull(message = "Event date is required.")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime eventDate;

  @NotNull(message = "Location is required.")
  private Location location;

  private Boolean paid = false;

  @PositiveOrZero(message = "Participant limit must be zero or a positive number.")
  private Integer participantLimit = 0;

  private Boolean requestModeration = true;

  @NotBlank(message = "Title is required and cannot be blank.")
  @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters.")
  private String title;

}
