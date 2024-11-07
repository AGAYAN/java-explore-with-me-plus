package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.StatusRequest;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

  private final RequestRepository requestRepository;
  private final UserRepository userRepository;
  private final EventRepository eventRepository;

  @Override
  public ParticipationRequestDto addRequest(Long userId, Long eventId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("Нету такого user"));

    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new NotFoundException("Нету такого event"));


    ParticipationRequest participationRequest = RequestMapper.mapToUser(new ParticipationRequestDto(), user, event);

    if (!(participationRequest.getStatus() == StatusRequest.PENDING)) {
      throw new NotFoundException("Заявка должна быть PENDING");
    }

    if (participationRequest.getRequester().getId().equals(event.getInitiator().getId())) {
      throw new NotFoundException("Запрос на свое событие запредено");
    }

    ParticipationRequest savedRequest = requestRepository.save(participationRequest);

    return RequestMapper.mapToUserDto(savedRequest);
  }

  @Override
  public List<ParticipationRequestDto> getRequestById(final Long userId, final Long eventId) {
    return List.of();
  }
}