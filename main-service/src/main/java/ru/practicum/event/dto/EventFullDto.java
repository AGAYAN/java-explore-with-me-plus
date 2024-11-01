package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.Location;

/**
 * ADMIN API - RESPONSE
 * GET /admin/events
 * PATCH admin/events/{eventId}
 *
 * PUBLIC API - RESPONSE
 * GET /events
 * GET /events/{id}
 *
 * PRIVATE API - RESPONSE
 * POST /users/{userId}/events
 * GET /users/{userId}/events/{eventId}
 * PATCH /users/{userId}/events/{eventId}
 */
@Data
public class EventFullDto {

  private String annotation;

  private CategoryDto category;

  private Integer confirmedRequests;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdOn;

  private String description;

  private LocalDateTime eventDate;

  private Long id;
// TODO Добавить как будет доступно
//  private UserShortDto initiator;

  private Location location;

  private Boolean paid;

  private Integer participantLimit = 0;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime publishedOn;

  private Boolean requestModeration = true;

  private String state;

  private String title;

  private Integer views;

}
