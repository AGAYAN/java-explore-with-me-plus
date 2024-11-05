package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.user.model.User;

import java.util.Objects;

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

}
