package ru.practicum.request.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private ParticipationRequestDto addRequest(@PathVariable @NonNull Long userId,
                                               @RequestParam @NonNull Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping
    private List<ParticipationRequestDto> getAllRequest(@PathVariable @NonNull Long userId) {
        return requestService.getAll(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    private ParticipationRequestDto cancel(@PathVariable @NonNull Long userId,
                                           @PathVariable @NonNull Long requestId) {
        return requestService.cancel(userId, requestId);
    }
}
