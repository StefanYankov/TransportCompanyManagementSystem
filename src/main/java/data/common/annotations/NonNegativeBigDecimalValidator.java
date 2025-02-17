package data.common.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class NonNegativeBigDecimalValidator implements ConstraintValidator<NonNegativeBigDecimal, BigDecimal> {

    @Override
    public void initialize(NonNegativeBigDecimal constraintAnnotation) {
        // Initialization logic (optional)
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;  // Null values are valid (can add @NotNull to handle this)
        }
        return value.signum() != -1;  // Returns true if the BigDecimal is not negative
    }
}