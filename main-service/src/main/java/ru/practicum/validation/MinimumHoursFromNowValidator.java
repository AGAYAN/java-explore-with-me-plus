package ru.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class MinimumHoursFromNowValidator implements ConstraintValidator<MinimumHoursFromNow, LocalDateTime> {

  private LocalDateTime minimumValidTime;

  @Override
  public void initialize(MinimumHoursFromNow constraintAnnotation) {
    long hoursInFuture = constraintAnnotation.hoursInFuture();
    minimumValidTime = LocalDateTime.now().plusHours(hoursInFuture);
  }

  @Override
  public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {
    if (eventDate == null) {
      return true;
    }
    return eventDate.isAfter(minimumValidTime) || eventDate.isEqual(minimumValidTime);
  }
}
