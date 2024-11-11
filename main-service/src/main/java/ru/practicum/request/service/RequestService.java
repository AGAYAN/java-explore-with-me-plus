package ru.practicum.request.service;

import java.util.List;
import ru.practicum.request.dto.ParticipationRequestDto;

public interface RequestService {

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAll(Long userId);

    ParticipationRequestDto cancel(Long userId, Long requestId);

}
