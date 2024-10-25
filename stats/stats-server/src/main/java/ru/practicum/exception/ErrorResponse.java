package ru.practicum.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status,
                             String reason,
                             String message,
                             String stackTrace) {

}
