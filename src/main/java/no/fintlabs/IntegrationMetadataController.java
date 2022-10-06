package no.fintlabs;


import no.fintlabs.model.fint.InstanceElementMetadata;
import no.fintlabs.model.fint.IntegrationMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;

import static no.fintlabs.resourceserver.UrlPaths.INTERNAL_API;

@RequestMapping(INTERNAL_API + "/metadata")
@RestController
public class IntegrationMetadataController {

    private final IntegrationMetadataRepository integrationMetadataRepository;

    public IntegrationMetadataController(IntegrationMetadataRepository integrationMetadataRepository) {
        this.integrationMetadataRepository = integrationMetadataRepository;
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

}
