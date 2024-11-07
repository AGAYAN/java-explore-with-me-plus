package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.StatusRequest;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Нету такого user"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Нету такого event"));

        int confirRequestRepository = requestRepository.countAllByEventIdAndStatus(eventId, StatusRequest.CONFIRMED);

        if(user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Нельзя добавить запрос на свое собственное событие");
        }

        ParticipationRequest participationRequest = RequestMapper.mapTo(new ParticipationRequestDto(), user, event);

        if(event.getParticipantLimit() != 0 && event.getParticipantLimit() == confirRequestRepository) {
            throw new ConflictException("Лимит запроса закончен");
        }

        if (!event.getRequestModeration()) {
            participationRequest.setStatus(StatusRequest.CONFIRMED);
        }

        ParticipationRequest savedRequest = requestRepository.save(participationRequest);

        return RequestMapper.mapToDto(savedRequest);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getAll(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Нету такого user"));

        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Нету такого user"));

        ParticipationRequest participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Нету такого запроса"));

        if(!userId.equals(participationRequest.getRequester().getId())) {
            throw new NotFoundException("Отменить может только владелец заявки");
        }

        participationRequest.setStatus(StatusRequest.CANCELED);

        requestRepository.save(participationRequest);

        return RequestMapper.mapToDto(participationRequest);
    }

    @Override
    public List<ParticipationRequestDto> getRequestById(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Нету такого user"));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Нету такого event"));

        return requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId)
                .stream()
                .map(RequestMapper::mapToDto)
                .toList();
    }

}
