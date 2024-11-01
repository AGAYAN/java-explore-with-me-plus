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

  @ExceptionHandler(Exception.class)
  @ResponseStatus
  public ResponseEntity<ApiError> handleExceptions(final Exception e) {
    HttpStatus status;
    String reason;

    if (e instanceof AlreadyExistsException) {
      status = HttpStatus.CONFLICT;
      reason = e.getMessage();
    } else if (e instanceof NotFoundException) {
      status = HttpStatus.NOT_FOUND;
      reason = e.getMessage();
    } else {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      reason = "Internal server error";
    }

    log.error("{} {}", status.value(), e.getMessage(), e);

    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    final String stackTrace = sw.toString();
    final ApiError apiError = new ApiError(
            status,
            reason,
            e.getMessage(),
            LocalDateTime.now(),
            stackTrace);

    return ResponseEntity.status(status)
        .body(apiError);

  }
}
