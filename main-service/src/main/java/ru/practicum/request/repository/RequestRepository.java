package ru.practicum.request.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.StatusRequest;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

  List<ParticipationRequest> findAllByRequesterId(Long userId);

  List<ParticipationRequest> findAllByEventIdAndEventInitiatorId(Long userId, Long initiatorId);

  int countAllByEventIdAndStatus(Long eventId, StatusRequest status);

  ParticipationRequest findByRequesterIdAndEventId(Long userId, Long eventId);

  List<ParticipationRequest> findAllByEventIdInAndStatus(List<Long> eventIds, StatusRequest statusRequest);

  List<ParticipationRequest> findAllByIdInAndEventIdAndStatus(
      List<Long> requestIds, Long eventId, StatusRequest statusRequest);

}
