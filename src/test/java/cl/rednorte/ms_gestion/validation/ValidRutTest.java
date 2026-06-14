package cl.rednorte.ms_gestion.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidRutTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        // Inicializamos el motor de validación estándar de Jakarta de forma nativa
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Clase interna Dummy (POJO) para aplicar la anotación y poder testearla
    private static class DummyDto {
        @ValidRut
        private final String rut;

        public DummyDto(String rut) {
            this.rut = rut;
        }
    }

    // ==========================================
    // TESTS: CAMINOS FELICES (RUTs Válidos)
    // ==========================================

    @Test
    @DisplayName("ValidRut -> Debe pasar sin errores con un RUT común válido")
    void rutValido_Comun_NoGeneraViolaciones() {
        // CORRECCIÓN: 12345678-5 es un RUT matemáticamente correcto en Chile
        DummyDto dto = new DummyDto("12345678-5");

        Set<ConstraintViolation<DummyDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Un RUT válido no debería generar errores.");
    }

    @Test
    @DisplayName("ValidRut -> Debe pasar sin errores con un RUT válido que termina en K")
    void rutValido_ConK_NoGeneraViolaciones() {
        // CORRECCIÓN: 19000001-K es un RUT con K matemáticamente correcto en Chile
        DummyDto dto = new DummyDto("19000001-K");

        Set<ConstraintViolation<DummyDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Un RUT con guión y K válido debería ser aceptado.");
    }

    // ==========================================
    // TESTS: CASOS BORDE / CAMINOS TRISTES
    // ==========================================

    @Test
    @DisplayName("ValidRut -> Debe fallar si el dígito verificador es incorrecto")
    void rutInvalido_DigitoVerificadorMalo_GeneraViolacion() {
        // 12345678 termina en 5, le ponemos un 0 intencionalmente para que falle
        DummyDto dto = new DummyDto("12345678-0"); 

        Set<ConstraintViolation<DummyDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Debería gatillar un error de validación.");
        assertEquals(1, violations.size());
        assertEquals("El RUT no es válido", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("ValidRut -> Debe fallar si el formato es un texto cualquiera")
    void rutInvalido_TextoCualquiera_GeneraViolacion() {
        DummyDto dto = new DummyDto("un-texto-invalido");

        Set<ConstraintViolation<DummyDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("ValidRut -> Debe fallar si viene completamente vacío")
    void rutInvalido_Vacio_GeneraViolacion() {
        DummyDto dto = new DummyDto(" ");

        Set<ConstraintViolation<DummyDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
    }
}