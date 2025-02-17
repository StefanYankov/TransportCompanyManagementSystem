package data.common.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Define the annotation as a constraint
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NonNegativeBigDecimalValidator.class)  // Link to the validator class
public @interface NonNegativeBigDecimal {

    String message() default "Value cannot be negative";  // Default error message

    Class<?>[] groups() default {};  // For grouping constraints

    Class<? extends Payload>[] payload() default {};  // Additional metadata for the annotation
}