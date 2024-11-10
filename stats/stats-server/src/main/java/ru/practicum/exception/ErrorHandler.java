package ru.practicum.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleBadRequest(final BadRequestException e) {
    log.error("Handling BadRequestException", e);
    log.error("400 Bad Request: {}", e.getMessage(), e);
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad request", e);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleInternalServerError(final Exception e) {
    log.error("500 Internal Server Error: {}", e.getMessage(), e);
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    String stackTrace = sw.toString();

    ErrorResponse errorResponse = new ErrorResponse(
            status,
            message,
            e.getMessage(),
            stackTrace
    );

    return ResponseEntity.status(status).body(errorResponse);
  }
}
