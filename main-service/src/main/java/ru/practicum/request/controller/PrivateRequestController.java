package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    private List<ParticipationRequestDto> getRequestById(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getRequestById(userId, eventId);
    }

}
