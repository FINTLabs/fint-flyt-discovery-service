package no.fintlabs;

import no.fintlabs.model.dtos.InstanceMetadataContentDto;
import no.fintlabs.model.dtos.IntegrationMetadataDto;
import no.fintlabs.model.entities.IntegrationMetadata;
import no.fintlabs.validation.ValidationErrorsFormattingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IntegrationMetadataControllerTest {

    private IntegrationMetadataController controller;
    @Mock
    private IntegrationMetadataService integrationMetadataService;

    @Mock
    private Validator validator;

    @Mock
    private ValidationErrorsFormattingService validationErrorsFormattingService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new IntegrationMetadataController(integrationMetadataService, validator, validationErrorsFormattingService);
    }

    @Test
    public void getIntegrationMetadataForSourceApplication_ReturnsBadRequest() {
        ResponseEntity<Collection<IntegrationMetadata>> response = controller.getIntegrationMetadataForSourceApplication();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void getIntegrationMetadataForSourceApplication_WithParams_ReturnsData() {
        IntegrationMetadataDto dto = mock(IntegrationMetadataDto.class);
        when(integrationMetadataService.getIntegrationMetadataForSourceApplication(1L, false))
                .thenReturn(Collections.singletonList(dto));

        ResponseEntity<Collection<IntegrationMetadataDto>> response =
                controller.getIntegrationMetadataForSourceApplication(1L, Optional.empty());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void getIntegrationMetadataForIntegration_WithBothParams_ReturnsData() {
        IntegrationMetadataDto dto = mock(IntegrationMetadataDto.class);
        when(integrationMetadataService.getAllForSourceApplicationIdAndSourceApplicationIntegrationId(1L, "integrationId"))
                .thenReturn(Collections.singletonList(dto));

        ResponseEntity<Collection<IntegrationMetadataDto>> response =
                controller.getIntegrationMetadataForIntegration(1L, "integrationId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void getInstanceElementMetadataForIntegrationMetadataWithId_ValidId_ReturnsData() {
        InstanceMetadataContentDto dto = mock(InstanceMetadataContentDto.class);
        when(integrationMetadataService.getInstanceMetadataById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<InstanceMetadataContentDto> response =
                controller.getInstanceElementMetadataForIntegrationMetadataWithId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void getInstanceElementMetadataForIntegrationMetadataWithId_InvalidId_ThrowsNotFound() {
        when(integrationMetadataService.getInstanceMetadataById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> controller.getInstanceElementMetadataForIntegrationMetadataWithId(1L));
    }

    @Test
    public void post_WithValidationErrors_ThrowsException() {
        IntegrationMetadataDto dto = mock(IntegrationMetadataDto.class);

        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<IntegrationMetadataDto>> violations = mock(Set.class);

        when(violations.isEmpty()).thenReturn(false);
        when(validator.validate(dto)).thenReturn(violations);

        assertThrows(ResponseStatusException.class, () -> controller.post(dto));
    }

    @Test
    public void post_ValidDto_SavesAndReturnsOk() {
        IntegrationMetadataDto dto = mock(IntegrationMetadataDto.class);
        Set<ConstraintViolation<IntegrationMetadataDto>> violations = new HashSet<>();
        when(validator.validate(dto)).thenReturn(violations);
        when(integrationMetadataService.versionExists(dto)).thenReturn(false);

        ResponseEntity<?> response = controller.post(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void post_VersionExists_ThrowsConflict() {
        IntegrationMetadataDto dto = mock(IntegrationMetadataDto.class);
        Set<ConstraintViolation<IntegrationMetadataDto>> violations = new HashSet<>();
        when(validator.validate(dto)).thenReturn(violations);
        when(integrationMetadataService.versionExists(dto)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> controller.post(dto));
    }

}