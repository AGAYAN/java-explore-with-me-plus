package ru.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class StatusRequestValidator implements ConstraintValidator<ValidateStatusRequest, String> {

  private List<String> validStatusOptions;

  @Override
  public void initialize(final ValidateStatusRequest constraintAnnotation) {
    validStatusOptions = Arrays.stream(constraintAnnotation.allowedValues())
        .map(Enum::name)
        .toList();
  }

  @Override
  public boolean isValid(final String statusValue, final ConstraintValidatorContext context) {
    if (statusValue == null || statusValue.isEmpty()) {
      return true;
    }
    return validStatusOptions.contains(statusValue.trim().toUpperCase());
  }
}
