package ru.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import ru.practicum.event.enums.Role;
import ru.practicum.event.enums.StateAction;

public class StateActionValidator implements ConstraintValidator<ValidStateAction, String> {

  private List<String> validStateOptions;

  @Override
  public void initialize(final ValidStateAction constraintAnnotation) {
    final Role role = constraintAnnotation.role();
    validStateOptions = StateAction.getValidStates(role);
  }

  @Override
  public boolean isValid(final String stateActionValue, final ConstraintValidatorContext context) {
    if (stateActionValue == null || stateActionValue.isEmpty()) {
      return true;
    }
    return validStateOptions.contains(stateActionValue.trim().toUpperCase());
  }
}
