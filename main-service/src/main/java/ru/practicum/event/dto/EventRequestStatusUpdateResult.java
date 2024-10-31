package ru.practicum.event.dto;

import java.util.List;

/**
 * RESPONSE
 * PRIVATE API
 *
 * PATCH /users/{userId}/events/{eventId}/requests
 */
public class EventRequestStatusUpdateResult {

  private List<ParticipationRequestDto> confirmedRequests;
  private List<ParticipationRequestDto> rejectedRequests;
 }
