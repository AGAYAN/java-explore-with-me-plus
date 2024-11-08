package ru.practicum.event.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.request.model.StatusRequest;

import java.util.List;


/**
 * PRIVATE - REQUEST body PATCH /users/{userId}/events/{eventId}/requests
 */
@Getter
@Setter
public class EventRequestStatusUpdateRequest {

  @NotNull
  @NotEmpty
  private List<Long> requestIds;

  @NotNull
  //  @ValidateRequestStatus
  private StatusRequest status;

}
