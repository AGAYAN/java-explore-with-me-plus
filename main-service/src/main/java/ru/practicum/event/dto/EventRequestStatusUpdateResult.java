package ru.practicum.event.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * RESPONSE
 * PRIVATE API
 *
 * PATCH /users/{userId}/events/{eventId}/requests
 */
@Getter
@Setter
@RequiredArgsConstructor
public class EventRequestStatusUpdateResult {

  private List<ParticipationRequestDto> confirmedRequests;
  private List<ParticipationRequestDto> rejectedRequests;
 }
