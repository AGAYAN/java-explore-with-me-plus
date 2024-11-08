package ru.practicum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = MinimumHoursFromNowValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinimumHoursFromNow {

  String message() default "The date must be at least two hours in the future.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  long hoursInFuture() default 2;

}
