package ru.practicum.request.repository;

import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.Model.ParticipationRequest;
import ru.practicum.request.Model.StatusRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(Long userId);

    List<ParticipationRequest> findAllByEventIdAndEventInitiatorId(Long userId, Long eventId);
}
