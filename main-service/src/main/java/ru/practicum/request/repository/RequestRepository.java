package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.StatusRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(Long userId);

    List<ParticipationRequest> findAllByEventIdAndEventInitiatorId(Long userId, Long eventId);

    int countAllByEventIdAndStatus(Long eventId, StatusRequest status);

    ParticipationRequest findByRequesterIdAndEventId(Long userId, Long eventId);

}
