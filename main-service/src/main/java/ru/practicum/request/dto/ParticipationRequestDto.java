package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * PRIVATE API - RESPONSE
 * <p>
 * GET /users/{userId}/requests
 * <p>
 * POST /users/{userId}/requests
 * <p>
 * PATCH /users/{userId}/requests/{requestId}/cancel
 * <p>
 * GET /users/{userId}/events/{eventId}/requests
 * <p>
 * PATCH /users/{userId}/events/{eventId}/requests
 *
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ParticipationRequestDto {

  private Long id;
  private Long event;
  private Long requester;
  private String status;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime created = LocalDateTime.now();
}
