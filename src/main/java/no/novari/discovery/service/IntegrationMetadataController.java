package no.novari.discovery.service;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import no.novari.discovery.service.model.dtos.InstanceMetadataContentDto;
import no.novari.discovery.service.model.dtos.IntegrationMetadataDto;
import no.novari.discovery.service.model.entities.IntegrationMetadata;
import no.novari.discovery.service.validation.ValidationErrorsFormattingService;
import no.novari.flyt.resourceserver.security.user.UserAuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static no.novari.flyt.resourceserver.UrlPaths.INTERNAL_API;

@RequestMapping(INTERNAL_API + "/metadata")
@RestController
public class IntegrationMetadataController {

    private final IntegrationMetadataService integrationMetadataService;

    private final Validator validator;
    private final ValidationErrorsFormattingService validationErrorsFormattingService;
    private final UserAuthorizationService userAuthorizationService;

    public IntegrationMetadataController(
            IntegrationMetadataService integrationMetadataService,
            Validator validator,
            ValidationErrorsFormattingService validationErrorsFormattingService,
            UserAuthorizationService userAuthorizationService
    ) {
        this.integrationMetadataService = integrationMetadataService;
        this.validator = validator;
        this.validationErrorsFormattingService = validationErrorsFormattingService;
        this.userAuthorizationService = userAuthorizationService;
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
        userAuthorizationService.checkIfUserHasAccessToSourceApplication(authentication, sourceApplicationId);

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
        userAuthorizationService.checkIfUserHasAccessToSourceApplication(authentication, sourceApplicationId);

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

        userAuthorizationService.checkIfUserHasAccessToSourceApplication(authentication, integrationMetadataDto.getSourceApplicationId());

        return ResponseEntity.ok(integrationMetadataDto.getInstanceMetadata());
    }

    @PostMapping
    public ResponseEntity<?> post(
            @AuthenticationPrincipal Authentication authentication,
            @RequestBody IntegrationMetadataDto integrationMetadataDto
    ) {
        userAuthorizationService.checkIfUserHasAccessToSourceApplication(authentication, integrationMetadataDto.getSourceApplicationId());

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
