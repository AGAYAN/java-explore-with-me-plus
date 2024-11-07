package ru.practicum.request.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.StatusRequest;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

  List<ParticipationRequest> findAllByEventIdAndEventInitiatorId(Long eventId, Long initiatorId);

  List<ParticipationRequest> findAllByEventIdInAndStatus(List<Long> eventIds, StatusRequest statusRequest);

  List<ParticipationRequest> findAllByIdInAndEventIdAndStatus(List<Long> requestIds, Long eventId, StatusRequest statusRequest);

}
