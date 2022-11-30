package no.fintlabs;


import no.fintlabs.model.InstanceElementMetadata;
import no.fintlabs.model.IntegrationMetadata;
import no.fintlabs.model.web.IntegrationMetadataPost;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final IntegrationMetadataRepository integrationMetadataRepository;
    private final IntegrationMetadataMapper integrationMetadataMapper;

    private final Validator validator;
    private final ValidationErrorsFormattingService validationErrorsFormattingService;

    public IntegrationMetadataController(
            IntegrationMetadataRepository integrationMetadataRepository,
            IntegrationMetadataMapper integrationMetadataMapper,
            Validator validator,
            ValidationErrorsFormattingService validationErrorsFormattingService
    ) {
        this.integrationMetadataRepository = integrationMetadataRepository;
        this.integrationMetadataMapper = integrationMetadataMapper;
        this.validator = validator;
        this.validationErrorsFormattingService = validationErrorsFormattingService;
    }

    @GetMapping(params = {"kildeapplikasjonId", "kildeapplikasjonIntegrasjonId", "bareSisteVersjoner"})
    public ResponseEntity<Collection<IntegrationMetadata>> getIntegrationMetadataForSourceApplication() {
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(params = {"kildeapplikasjonId"})
    public ResponseEntity<Collection<IntegrationMetadata>> getIntegrationMetadataForSourceApplication(
            @RequestParam(name = "kildeapplikasjonId") Long sourceApplicationId,
            @RequestParam(name = "bareSisteVersjoner") Optional<Boolean> onlyLatestVersions
    ) {
        Collection<IntegrationMetadata> integrationMetadata =
                onlyLatestVersions.orElse(false)
                        ? integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(sourceApplicationId)
                        : integrationMetadataRepository.findAllBySourceApplicationId(sourceApplicationId);
        return ResponseEntity.ok(integrationMetadata);
    }

    @GetMapping(params = {"kildeapplikasjonId", "kildeapplikasjonIntegrasjonId"})
    public ResponseEntity<Collection<IntegrationMetadata>> getIntegrationMetadataForIntegration(
            @RequestParam(name = "kildeapplikasjonId") Long sourceApplicationId,
            @RequestParam(name = "kildeapplikasjonIntegrasjonId") String sourceApplicationIntegrationId
    ) {
        Collection<IntegrationMetadata> integrationMetadata = integrationMetadataRepository
                .findAllBySourceApplicationIdAndSourceApplicationIntegrationId(
                        sourceApplicationId,
                        sourceApplicationIntegrationId
                );
        return ResponseEntity.ok(integrationMetadata);
    }

    @GetMapping("{metadataId}/instans-element-metadata")
    public ResponseEntity<Collection<InstanceElementMetadata>> getInstanceElementMetadataForIntegrationMetadataWithId(
            @PathVariable Long metadataId
    ) {
        Collection<InstanceElementMetadata> instanceElementMetadata = integrationMetadataRepository
                .findById(metadataId)
                .map(IntegrationMetadata::getInstanceElementMetadata)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(instanceElementMetadata);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody IntegrationMetadataPost integrationMetadataPost) {
        Set<ConstraintViolation<IntegrationMetadataPost>> constraintViolations = validator.validate(integrationMetadataPost);
        if (!constraintViolations.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    validationErrorsFormattingService.format(constraintViolations)
            );
        }

        IntegrationMetadata integrationMetadata =
                integrationMetadataMapper.toIntegrationMetadata(integrationMetadataPost);

        if (integrationMetadataRepository.existsBySourceApplicationIdAndAndSourceApplicationIntegrationIdAndVersion(
                integrationMetadata.getSourceApplicationId(),
                integrationMetadata.getSourceApplicationIntegrationId(),
                integrationMetadata.getVersion()
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Version already exists");
        }
        integrationMetadataRepository.save(integrationMetadata);
        return ResponseEntity.ok().build();
    }

}
