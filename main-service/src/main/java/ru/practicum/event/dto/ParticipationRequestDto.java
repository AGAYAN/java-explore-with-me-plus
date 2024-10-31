package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * PRIVATE API - RESPONSE
 * GET /users/{userId}/events/{eventId}/requests
 */
public class ParticipationRequestDto {

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime created;
  private Long event;
  private Long id;
  private Long requester;
  private String status;
}
