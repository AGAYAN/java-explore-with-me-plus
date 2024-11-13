package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

/**
 * Used in the PRIVATE API - as RESPONSE
 * <p> GET /users/{userId}/events
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class EventShortDto {

  private String annotation;
  private CategoryDto category;
  private Integer confirmedRequests;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime eventDate;
  private Long id;
  private UserShortDto initiator;
  private Boolean paid;
  private String title;
  private Long views;

  public EventShortDto(String annotation, CategoryDto category, LocalDateTime eventDate, Long id, UserShortDto initiator, Boolean paid, String title) {
    this.annotation = annotation;
    this.category = category;
    this.eventDate = eventDate;
    this.id = id;
    this.initiator = initiator;
    this.paid = paid;
    this.title = title;
  }
}
