package com.example.settlementsenrichment.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DigitsOnlyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DigitsOnly {
    String message() default "{field} must contain only digits";

    String field();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}