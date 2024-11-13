package ru.practicum.event.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.request.dto.ParticipationRequestDto;

/**
 * Used in the PRIVATE API as RESPONSE
 * <p>
 * PATCH /users/{userId}/events/{eventId}/requests
 */
@Data
@Accessors(chain = true)
public class EventRequestStatusUpdateResult {

  private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
  private List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
 }
