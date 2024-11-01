package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

/**
 * ADMIN API - RESPONSE GET /admin/events PATCH admin/events/{eventId}
 * <p>
 * PUBLIC API - RESPONSE GET /events GET /events/{id}
 * <p>
 * PRIVATE API - RESPONSE POST /users/{userId}/events GET /users/{userId}/events/{eventId} PATCH
 * /users/{userId}/events/{eventId}
 */
@Data
@Accessors(chain = true)
public class EventFullDto {

  private String annotation;

  private CategoryDto category;

  private Integer confirmedRequests;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdOn;

  private String description;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime eventDate;

  private Long id;

  private UserShortDto initiator;

  private Location location;

  private Boolean paid;

  private Integer participantLimit = 0;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime publishedOn;

  private Boolean requestModeration = true;

  private String state;

  private String title;

  private Long views;

}
