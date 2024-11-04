package ru.practicum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ru.practicum.event.enums.Role;

@Constraint(validatedBy = StateActionValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStateAction {

  String message() default "Invalid state action.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  Role role() default Role.USER;

}
