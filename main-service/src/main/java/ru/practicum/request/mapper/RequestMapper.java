package ru.practicum.request.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

@UtilityClass
@Slf4j
public class RequestMapper {

  public ParticipationRequestDto mapToDto(final ParticipationRequest participationRequest) {
    log.debug("Mapping participationRequestDto {} to RequestDto.", participationRequest);
    Objects.requireNonNull(participationRequest);
    return new ParticipationRequestDto()
        .setId(participationRequest.getId())
        .setRequester(participationRequest.getRequester().getId())
        .setEvent(participationRequest.getEvent().getId())
        .setCreated(participationRequest.getCreated())
        .setStatus(participationRequest.getStatus().name());
  }

  public List<ParticipationRequestDto> mapToDto(final List<ParticipationRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      return Collections.emptyList();
    }
    return requests.stream()
        .map(RequestMapper::mapToDto)
        .toList();
  }

}
