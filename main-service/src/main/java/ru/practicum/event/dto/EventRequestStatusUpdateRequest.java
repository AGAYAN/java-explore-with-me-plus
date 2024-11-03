package ru.practicum.event.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;


/**
 * PRIVATE - REQUEST body PATCH /users/{userId}/events/{eventId}/requests
 */
public class EventRequestStatusUpdateRequest {

  @NotNull
  @NotEmpty
  private List<Long> requestIds;

  @NotNull
  //  @ValidateRequestStatus
  private String status;

}
