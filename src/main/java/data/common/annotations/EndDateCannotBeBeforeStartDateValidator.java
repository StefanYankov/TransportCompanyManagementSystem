package data.common.annotations;

import data.models.transportservices.TransportService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class EndDateCannotBeBeforeStartDateValidator implements ConstraintValidator<EndDateCannotBeBeforeStartDate, TransportService> {

    @Override
    public void initialize(EndDateCannotBeBeforeStartDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(TransportService destination, ConstraintValidatorContext context) {

        LocalDate startingDate = destination.getStartingDate();
        LocalDate endingDate = destination.getEndingDate();

        if (startingDate != null && endingDate != null) {
            if (endingDate.isBefore(startingDate)) {
                return false;
            }
            return true;
        }

        return true;
    }
}
