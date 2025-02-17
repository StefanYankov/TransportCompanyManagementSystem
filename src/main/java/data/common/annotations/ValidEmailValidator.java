package data.common.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    private Pattern pattern;
    private Matcher matcher;

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        pattern = Pattern.compile(EMAIL_REGEX);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
