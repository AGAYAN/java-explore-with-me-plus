package ru.practicum.exception;

public class StatsBadRequestException extends RuntimeException {
    public StatsBadRequestException(String message) {
        super(message);
    }
}