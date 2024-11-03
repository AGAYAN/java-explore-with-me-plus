package ru.practicum.request.controller;

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
public class PublicRequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private ParticipationRequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping
    private List<ParticipationRequestDto> getAllRequest(@PathVariable Long userId) {
        return requestService.getAll(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    private ParticipationRequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancel(userId, requestId);
    }
}
