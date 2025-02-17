package data.common.annotations;

import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EndDateCannotBeBeforeStartDate {

    String message() default "End date cannot be before the start date ";  // Default error message

    Class<?>[] groups() default {};  // For grouping constraints

    Class<? extends Payload>[] payload() default {};  // Additional metadata for the annotation
}
