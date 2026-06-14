package cl.rednorte.ms_gestion.exception;

import java.util.Map;

public class FormValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public FormValidationException(Map<String, String> errors) {
        super("La validación de datos falló");
        this.errors = new java.util.HashMap<>(errors);
        if (!errors.containsKey("error")) {
            this.errors.put("error", String.join(", ", errors.values()));
        }
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
