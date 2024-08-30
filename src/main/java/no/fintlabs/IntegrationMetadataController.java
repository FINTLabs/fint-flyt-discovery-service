package no.fintlabs;


import no.fintlabs.model.dtos.InstanceMetadataContentDto;
import no.fintlabs.model.dtos.IntegrationMetadataDto;
import no.fintlabs.model.entities.IntegrationMetadata;
import no.fintlabs.resourceserver.security.user.UserAuthorizationUtil;
import no.fintlabs.validation.ValidationErrorsFormattingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static no.fintlabs.resourceserver.UrlPaths.INTERNAL_API;

@RequestMapping(INTERNAL_API + "/metadata")
@RestController
public class IntegrationMetadataController {

    private final IntegrationMetadataService integrationMetadataService;

    private final Validator validator;
    private final ValidationErrorsFormattingService validationErrorsFormattingService;
    @Value("${fint.flyt.resource-server.user-permissions-consumer.enabled:false}")
    private boolean userPermissionsConsumerEnabled;

    public IntegrationMetadataController(
            IntegrationMetadataService integrationMetadataService,
            Validator validator,
            ValidationErrorsFormattingService validationErrorsFormattingService
    ) {
        this.integrationMetadataService = integrationMetadataService;
        this.validator = validator;
        this.validationErrorsFormattingService = validationErrorsFormattingService;
    }

    @GetMapping(params = {"kildeapplikasjonId", "kildeapplikasjonIntegrasjonId", "bareSisteVersjoner"})
    public ResponseEntity<Collection<IntegrationMetadata>> getIntegrationMetadataForSourceApplication() {
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(params = {"kildeapplikasjonId"})
    public ResponseEntity<Collection<IntegrationMetadataDto>> getIntegrationMetadataForSourceApplication(
            @AuthenticationPrincipal Authentication authentication,
            @RequestParam(name = "kildeapplikasjonId") Long sourceApplicationId,
            @RequestParam(name = "bareSisteVersjoner") Optional<Boolean> onlyLatestVersions
    ) {
        if (userPermissionsConsumerEnabled) {
            UserAuthorizationUtil.checkIfUserHasAccessToSourceApplication(authentication, sourceApplicationId);
        }

        Collection<IntegrationMetadataDto> integrationMetadata =
                integrationMetadataService.getIntegrationMetadataForSourceApplication(
                        sourceApplicationId,
                        onlyLatestVersions.orElse(false)
                );

        return ResponseEntity.ok(integrationMetadata);
    }

    @GetMapping(params = {"kildeapplikasjonId", "kildeapplikasjonIntegrasjonId"})
    public ResponseEntity<Collection<IntegrationMetadataDto>> getIntegrationMetadataForIntegration(
            @AuthenticationPrincipal Authentication authentication,
            @RequestParam(name = "kildeapplikasjonId") Long sourceApplicationId,
            @RequestParam(name = "kildeapplikasjonIntegrasjonId") String sourceApplicationIntegrationId
    ) {
        if (userPermissionsConsumerEnabled) {
            UserAuthorizationUtil.checkIfUserHasAccessToSourceApplication(authentication, sourceApplicationId);
        }

        Collection<IntegrationMetadataDto> integrationMetadata =
                integrationMetadataService.getAllForSourceApplicationIdAndSourceApplicationIntegrationId(
                        sourceApplicationId,
                        sourceApplicationIntegrationId
                );
        return ResponseEntity.ok(integrationMetadata);
    }

    @GetMapping("{metadataId}/instans-metadata")
    public ResponseEntity<InstanceMetadataContentDto> getInstanceElementMetadataForIntegrationMetadataWithId(
            @AuthenticationPrincipal Authentication authentication,
            @PathVariable Long metadataId
    ) {
        IntegrationMetadataDto integrationMetadataDto = integrationMetadataService
                .getById(metadataId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (userPermissionsConsumerEnabled) {
            UserAuthorizationUtil.checkIfUserHasAccessToSourceApplication(authentication, integrationMetadataDto.getSourceApplicationId());
        }

        return ResponseEntity.ok(integrationMetadataDto.getInstanceMetadata());
    }

    @PostMapping
    public ResponseEntity<?> post(
            @AuthenticationPrincipal Authentication authentication,
            @RequestBody IntegrationMetadataDto integrationMetadataDto
    ) {
        if (userPermissionsConsumerEnabled) {
            UserAuthorizationUtil.checkIfUserHasAccessToSourceApplication(authentication, integrationMetadataDto.getSourceApplicationId());
        }

        Set<ConstraintViolation<IntegrationMetadataDto>> constraintViolations = validator.validate(integrationMetadataDto);
        if (!constraintViolations.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    validationErrorsFormattingService.format(constraintViolations)
            );
        }
        if (integrationMetadataService.versionExists(integrationMetadataDto)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Version already exists");
        }
        integrationMetadataService.save(integrationMetadataDto);
        return ResponseEntity.ok().build();
    }

}
