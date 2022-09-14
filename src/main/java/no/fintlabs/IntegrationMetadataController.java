package no.fintlabs;


import no.fintlabs.model.fint.IntegrationMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static no.fintlabs.resourceserver.UrlPaths.INTERNAL_API;

@RequestMapping(INTERNAL_API + "/integrasjon/metadata")
@RestController
public class IntegrationMetadataController {

    private final IntegrationMetadataService integrationMetadataService;

    public IntegrationMetadataController(IntegrationMetadataService integrationMetadataService) {
        this.integrationMetadataService = integrationMetadataService;
    }

    @GetMapping
    public ResponseEntity<Collection<IntegrationMetadata>> getIntegrationMetadataForSourceApplication(
            @RequestParam String sourceApplicationId
    ) {
        Collection<IntegrationMetadata> integrationMetadata =
                integrationMetadataService.findLatestVersionForSourceApplication(
                        sourceApplicationId
                );
        return ResponseEntity.ok(integrationMetadata);
    }

}
