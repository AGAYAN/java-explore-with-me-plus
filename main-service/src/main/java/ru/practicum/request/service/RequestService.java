package ru.practicum.request.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAll(Long userId);

    ParticipationRequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId);

    List<ParticipationRequestDto> getRequestById(@PathVariable Long userId, @PathVariable Long eventId);
}
