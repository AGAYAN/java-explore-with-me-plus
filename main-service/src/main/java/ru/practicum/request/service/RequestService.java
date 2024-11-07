package ru.practicum.request.service;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.request.dto.ParticipationRequestDto;

public interface RequestService {

  ParticipationRequestDto addRequest(Long userId, Long eventId);

  List<ParticipationRequestDto> getRequestById(@PathVariable Long userId, @PathVariable Long eventId);
}
