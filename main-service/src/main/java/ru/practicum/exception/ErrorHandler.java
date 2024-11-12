package ru.practicum.exception;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiError> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
    return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiError> handleMissingServletRequestParameter(final MissingServletRequestParameterException exception) {
    return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiError> handleBadRequestException(final BadRequestException exception) {
    log.error("400 Bad Request: {}", exception.getMessage(), exception);
    return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ApiError> handleNotFoundException(final NotFoundException exception) {
    return buildErrorResponse(exception, HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ApiError> handleAlreadyExistsException(final ConflictException exception) {
    return buildErrorResponse(exception, HttpStatus.CONFLICT, exception.getMessage());
  }

  @ExceptionHandler(AlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ApiError> handleAlreadyExistsException(final AlreadyExistsException exception) {
    return buildErrorResponse(exception, HttpStatus.CONFLICT, exception.getMessage());
  }


  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ApiError> handleGenericException(final Exception e) {
    return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
  }

  private ResponseEntity<ApiError> buildErrorResponse(final Exception exception, HttpStatus status, String reason) {
    log.error("{}: {}", status.value(), reason, exception);

    final String stackTrace = ExceptionUtils.getStackTrace(exception);

    ApiError apiError = new ApiError(
        status,
        reason,
        exception.getMessage(),
        LocalDateTime.now(),
        stackTrace
    );

    return ResponseEntity.status(status).body(apiError);
  }
}
