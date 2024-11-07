package ru.practicum.request.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.user.model.User;

@UtilityClass
@Slf4j
public class RequestMapper {

  public static ParticipationRequest mapToUser(final ParticipationRequestDto participationRequest, final User user, final Event event) {
    log.debug("Mapping participationRequest {} to UserDto.", participationRequest);
    Objects.requireNonNull(participationRequest);
    return new ParticipationRequest()
        .setId(participationRequest.getId())
        .setRequester(user)
        .setEvent(event)
        .setCreated(participationRequest.getCreated());


  }

  public static ParticipationRequestDto mapToUserDto(final ParticipationRequest participationRequest) {
    log.debug("Mapping participationRequestDto {} to UserDto.", participationRequest);
    Objects.requireNonNull(participationRequest);
    return new ParticipationRequestDto()
        .setId(participationRequest.getId())
        .setRequester(participationRequest.getRequester().getId())
        .setEvent(participationRequest.getEvent().getId())
        .setCreated(participationRequest.getCreated())
        .setStatus(participationRequest.getStatus().name());
  }

  public static List<ParticipationRequestDto> mapToDto(final List<ParticipationRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      return Collections.emptyList();
    }
    return requests.stream()
        .map(RequestMapper::mapToUserDto)
        .toList();
  }

}
