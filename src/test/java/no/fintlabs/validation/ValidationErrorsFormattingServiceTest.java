package no.fintlabs.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidationErrorsFormattingServiceTest {

    private ValidationErrorsFormattingService service;

    @BeforeEach
    public void setUp() {
        service = new ValidationErrorsFormattingService();
    }

    @Test
    public void format_WithSingleError_FormatsCorrectly() {
        Set<ConstraintViolation<Object>> errors = new HashSet<>();
        errors.add(createConstraintViolation("field1", "error message 1"));

        String result = service.format(errors);

        assertEquals("Validation error: ['field1 error message 1']", result);
    }

    @Test
    public void format_WithMultipleErrors_FormatsAndSortsCorrectly() {
        Set<ConstraintViolation<Object>> errors = new HashSet<>();
        errors.add(createConstraintViolation("field2", "error message 2"));
        errors.add(createConstraintViolation("field1", "error message 1"));

        String result = service.format(errors);

        assertEquals("Validation errors: ['field1 error message 1', 'field2 error message 2']", result);
    }

    @Test
    public void format_WithBlankField_FormatsCorrectly() {
        Set<ConstraintViolation<Object>> errors = new HashSet<>();
        errors.add(createConstraintViolation("", "error message 1"));

        String result = service.format(errors);

        assertEquals("Validation error: ['error message 1']", result);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ConstraintViolation<Object> createConstraintViolation(String field, String message) {
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class); // Explicit type not provided

        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn(field);
        when(violation.getMessage()).thenReturn(message);
        when(violation.getConstraintDescriptor()).thenReturn(descriptor); // This line should not throw an error now

        return violation;
    }

}
