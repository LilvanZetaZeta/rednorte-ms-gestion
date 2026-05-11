package cl.rednorte.ms_gestion.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RutValidator implements ConstraintValidator<ValidRut, String> {

    private static final Pattern RUT_PATTERN = Pattern.compile("^[0-9]+-[0-9kK]{1}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        if (!RUT_PATTERN.matcher(value).matches()) {
            return false;
        }

        String[] parts = value.split("-");
        String rutNumber = parts[0];
        String verifier = parts[1].toLowerCase();

        String expectedVerifier = calculateRutVerifier(rutNumber);

        return verifier.equals(expectedVerifier);
    }

    private String calculateRutVerifier(String rutNumber) {
        int sum = 0;
        int multiplier = 2;

        for (int i = rutNumber.length() - 1; i >= 0; i--) {
            sum += Character.getNumericValue(rutNumber.charAt(i)) * multiplier;
            multiplier = multiplier == 7 ? 2 : multiplier + 1;
        }

        int mod = 11 - (sum % 11);
        if (mod == 11)
            return "0";
        if (mod == 10)
            return "k";
        return String.valueOf(mod);
    }
}
