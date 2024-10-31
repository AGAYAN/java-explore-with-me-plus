package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * PRIVATE API - RESPONSE
 * GET /users/{userId}/events
 */
@Data
public class EventShortDto {

  private String annotation;
  // TODO Добавить как будет доступно
  //  private CategoryDto category;
  private Long confirmedRequests;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime eventDate;
  private Long id;
  // TODO Добавить как будет доступно
  //  private UserShortDto initiator;
  private Boolean paid;
  private String title;
  private Integer views;
}
