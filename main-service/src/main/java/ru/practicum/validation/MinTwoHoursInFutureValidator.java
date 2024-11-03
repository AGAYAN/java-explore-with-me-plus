package ru.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class MinTwoHoursInFutureValidator implements ConstraintValidator<MinTwoHoursInFuture, LocalDateTime> {

  private LocalDateTime minimumValidTime;

  @Override
  public void initialize(MinTwoHoursInFuture constraintAnnotation) {
    minimumValidTime = LocalDateTime.now().plusHours(2);
  }

  @Override
  public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {
    if (eventDate == null) {
      return true;
    }
    return eventDate.isAfter(minimumValidTime) || eventDate.isEqual(minimumValidTime);
  }
}
