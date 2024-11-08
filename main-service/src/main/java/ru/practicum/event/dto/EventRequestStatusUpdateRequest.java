package ru.practicum.event.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import ru.practicum.request.model.StatusRequest;
import ru.practicum.validation.ValidateStatusRequest;


/**
 * PRIVATE - REQUEST body PATCH /users/{userId}/events/{eventId}/requests
 */
@Data
public class EventRequestStatusUpdateRequest {

  @NotNull(message = "RequestIds cannot be null.")
  @NotEmpty(message = "RequestIds cannot be empty.")
  private List<Long> requestIds;

  @NotNull(message = "Status cannot be null.")
  @ValidateStatusRequest(allowedValues = {StatusRequest.CONFIRMED,StatusRequest.REJECTED})
  private StatusRequest status;

}
