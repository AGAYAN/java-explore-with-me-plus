package ru.practicum.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ApiError> handleExceptions(final Exception e) {
    log.error("500 {}", e.getMessage(), e);
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    final String stackTrace = sw.toString();
    final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal server error.", e.getMessage(), LocalDateTime.now(),stackTrace);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(apiError);

  }


}
