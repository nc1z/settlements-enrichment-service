package com.example.settlementsenrichment.validator;

import com.example.settlementsenrichment.util.ValidationConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DigitsOnlyValidator implements ConstraintValidator<DigitsOnly, String> {

    private String field;

    @Override
    public void initialize(DigitsOnly constraintAnnotation) {
        this.field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull should handle nulls
        }
        boolean isValid = value.matches(ValidationConstants.NUMERIC_REGEX);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(field + " must contain only digits")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
