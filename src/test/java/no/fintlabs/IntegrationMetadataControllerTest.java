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
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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
    Authentication authentication;

    @Mock
    private ValidationErrorsFormattingService validationErrorsFormattingService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new IntegrationMetadataController(integrationMetadataService, validator, validationErrorsFormattingService);
    }

    @Test
    public void shouldReturnBadRequestOnGetIntegrationMetadataForSourceApplication() {
        ResponseEntity<Collection<IntegrationMetadata>> response = controller.getIntegrationMetadataForSourceApplication();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldReturnHttp200IfIntegrationMetadataIsFound() {
        IntegrationMetadataDto dto = mock(IntegrationMetadataDto.class);
        when(integrationMetadataService.getIntegrationMetadataForSourceApplication(1L, false))
                .thenReturn(Collections.singletonList(dto));

        ResponseEntity<Collection<IntegrationMetadataDto>> response =
                controller.getIntegrationMetadataForSourceApplication(authentication, 1L, Optional.empty());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void shouldThrowExceptionForbiddenIfUserDontHaveAccessToIntegrationMetadata() throws NoSuchFieldException, IllegalAccessException {
        setUserPermissionsConsumerEnabled();

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sourceApplicationIds")).thenReturn("2");

        when(authentication.getPrincipal()).thenReturn(jwt);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getIntegrationMetadataForSourceApplication(authentication, 1L, Optional.empty())
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You do not have permission to access or modify data that is related to source application with id=1", exception.getReason());
    }

    @Test
    public void shouldReturnHttp200IfIntegrationMetadataIsFoundWithSourceApplicationIdAndSourceApplicationIntegrationIdAsParams() {
        IntegrationMetadataDto dto = mock(IntegrationMetadataDto.class);
        when(integrationMetadataService.getAllForSourceApplicationIdAndSourceApplicationIntegrationId(1L, "integrationId"))
                .thenReturn(Collections.singletonList(dto));

        ResponseEntity<Collection<IntegrationMetadataDto>> response =
                controller.getIntegrationMetadataForIntegration(authentication, 1L, "integrationId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void shouldThrowExceptionForbiddenIfUserDontHaveAccessToIntegrationMetadataWithSourceApplicationIdAndSourceApplicationIntegrationIdAsParams() throws NoSuchFieldException, IllegalAccessException {
        setUserPermissionsConsumerEnabled();

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sourceApplicationIds")).thenReturn("2");

        when(authentication.getPrincipal()).thenReturn(jwt);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getIntegrationMetadataForSourceApplication(authentication, 1L, Optional.empty())
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You do not have permission to access or modify data that is related to source application with id=1", exception.getReason());
    }

    @Test
    public void shouldReturnHttp200IfInstanceMetadataContentIsFound() {
        IntegrationMetadataDto integrationMetadataDto = mock(IntegrationMetadataDto.class);
        InstanceMetadataContentDto instanceMetadataContentDto = mock(InstanceMetadataContentDto.class);

        when(integrationMetadataService.getById(1L)).thenReturn(Optional.of(integrationMetadataDto));
        when(integrationMetadataDto.getInstanceMetadata()).thenReturn(instanceMetadataContentDto);

        ResponseEntity<InstanceMetadataContentDto> response =
                controller.getInstanceElementMetadataForIntegrationMetadataWithId(authentication, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void shouldThrowResponseStatusExceptionNotFoundWhenGetInstanceElementMetadataForIntegrationMetadataWithIdHasInvalidId() {
        when(integrationMetadataService.getInstanceMetadataById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> controller.getInstanceElementMetadataForIntegrationMetadataWithId(authentication, 1L));
    }

    @Test
    public void shouldThrowExceptionForbiddenIfUserDontHaveAccessToInstanceMetadataContent() throws NoSuchFieldException, IllegalAccessException {
        setUserPermissionsConsumerEnabled();

        Long metadataId = 1L;

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sourceApplicationIds")).thenReturn("2");

        when(authentication.getPrincipal()).thenReturn(jwt);

        IntegrationMetadataDto integrationMetadataDto = mock(IntegrationMetadataDto.class);
        when(integrationMetadataDto.getSourceApplicationId()).thenReturn(metadataId);

        when(integrationMetadataService.getById(metadataId)).thenReturn(Optional.of(integrationMetadataDto));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getInstanceElementMetadataForIntegrationMetadataWithId(authentication, metadataId)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You do not have permission to access or modify data that is related to source application with id=1", exception.getReason());
    }

    @Test
    public void shouldReturnResponseStatusExceptionIfPostHasValidationErrors() {
        IntegrationMetadataDto dto = mock(IntegrationMetadataDto.class);

        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<IntegrationMetadataDto>> violations = mock(Set.class);

        when(violations.isEmpty()).thenReturn(false);
        when(validator.validate(dto)).thenReturn(violations);

        assertThrows(ResponseStatusException.class, () -> controller.post(authentication, dto));
    }

    @Test
    public void shouldThrowExceptionForbiddenIfUserDontHaveAccessToIntegrationMetadataOnPost() throws NoSuchFieldException, IllegalAccessException {
        setUserPermissionsConsumerEnabled();

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sourceApplicationIds")).thenReturn("2");

        when(authentication.getPrincipal()).thenReturn(jwt);

        IntegrationMetadataDto integrationMetadataDto = mock(IntegrationMetadataDto.class);
        when(integrationMetadataDto.getSourceApplicationId()).thenReturn(1L);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.post(authentication, integrationMetadataDto)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You do not have permission to access or modify data that is related to source application with id=1", exception.getReason());
    }

    @Test
    public void shouldPostIntegrationMetadataWithNoErrors() {
        IntegrationMetadataDto dto = mock(IntegrationMetadataDto.class);
        Set<ConstraintViolation<IntegrationMetadataDto>> violations = new HashSet<>();
        when(validator.validate(dto)).thenReturn(violations);
        when(integrationMetadataService.versionExists(dto)).thenReturn(false);

        ResponseEntity<?> response = controller.post(authentication, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldThrowResponseStatusExceptionConflictWhenVersionExistsOnPost() {
        IntegrationMetadataDto dto = mock(IntegrationMetadataDto.class);
        Set<ConstraintViolation<IntegrationMetadataDto>> violations = new HashSet<>();
        when(validator.validate(dto)).thenReturn(violations);
        when(integrationMetadataService.versionExists(dto)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> controller.post(authentication, dto));
    }

    private void setUserPermissionsConsumerEnabled() throws NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Field field = IntegrationMetadataController.class.getDeclaredField("userPermissionsConsumerEnabled");
        field.setAccessible(true);
        field.set(controller, true);
    }

}