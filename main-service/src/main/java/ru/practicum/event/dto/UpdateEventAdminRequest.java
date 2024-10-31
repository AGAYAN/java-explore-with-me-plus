package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import ru.practicum.event.model.Location;

/**
 * ADMIN API - REQUEST body PATCH admin/events/{eventId}
 */
@Data
public class UpdateEventAdminRequest {

  @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters.")
  private String annotation;

  @Positive(message = "Category must be a positive number.")
  private Long category;

  @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters.")
  private String description;

  // TODO @FutureMinTwoHours
  @Future(message = "Event date must be in the future.")//in two hours at least?
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime eventDate;

  private Location location;

  private Boolean paid;

  @Positive(message = "Participant limit must be a positive number.")
  private Integer participantLimit;

  private Boolean requestModeration;
  //  TODO @ValidStateAction
  private String stateAction;

  @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters.")
  private String title;

}
