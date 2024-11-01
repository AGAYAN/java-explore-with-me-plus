package ru.practicum.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

//  @ExceptionHandler(Exception.class)
//  @ResponseStatus
//  public ResponseEntity<ApiError> handleExceptions(final Exception e) {
//    HttpStatus status;
//    String reason;
//
//    if (e instanceof AlreadyExistsException) {
//      status = HttpStatus.CONFLICT;
//      reason = e.getMessage();
//    } else if (e instanceof NotFoundException) {
//      status = HttpStatus.NOT_FOUND;
//      reason = e.getMessage();
//    } else {
//      status = HttpStatus.INTERNAL_SERVER_ERROR;
//      reason = "Internal server error";
//    }
//
//    log.error("{} {}", status.value(), e.getMessage(), e);
//
//    final StringWriter sw = new StringWriter();
//    final PrintWriter pw = new PrintWriter(sw);
//    e.printStackTrace(pw);
//    final String stackTrace = sw.toString();
//    final ApiError apiError = new ApiError(
//            status,
//            reason,
//            e.getMessage(),
//            LocalDateTime.now(),
//            stackTrace);
//
//    return ResponseEntity.status(status)
//        .body(apiError);
//
//  }

  @ExceptionHandler(AlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ApiError> handleAlreadyExistsException(final AlreadyExistsException exception) {
    return buildErrorResponse(exception, HttpStatus.CONFLICT, exception.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ApiError> handleNotFoundException(final NotFoundException exception) {
    return buildErrorResponse(exception, HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiError> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
    return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, exception.getMessage());
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
