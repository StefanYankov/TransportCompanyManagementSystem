package data.common.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmailValidator.class)
public @interface ValidEmail {

    // Error message when validation fails
    String message() default "Invalid email format";

    // Grouping constraints (optional)
    Class<?>[] groups() default {};

    // Additional data (optional)
    Class<? extends Payload>[] payload() default {};
}
