package ru.practicum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ru.practicum.request.model.StatusRequest;

@Constraint(validatedBy = StatusRequestValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateStatusRequest {

  String message() default "Invalid participation request status.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  StatusRequest[] allowedValues() default {StatusRequest.CONFIRMED, StatusRequest.REJECTED};


}
