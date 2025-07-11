package com.ipin.identity.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { DobValidator.class })
public @interface DobConstraint {
    String message() default "Invalid date of birth";

    int min();

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}