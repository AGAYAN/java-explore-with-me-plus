package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

/**
 * PRIVATE API - RESPONSE GET /users/{userId}/events
 */
@Data
@AllArgsConstructor
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
}
