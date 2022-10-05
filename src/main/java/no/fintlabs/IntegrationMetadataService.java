package no.fintlabs;

import no.fintlabs.model.fint.IntegrationMetadata;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class IntegrationMetadataService {

    private final IntegrationMetadataRepository integrationMetadataRepository;

    public IntegrationMetadataService(IntegrationMetadataRepository integrationMetadataRepository) {
        this.integrationMetadataRepository = integrationMetadataRepository;
    }

    public IntegrationMetadata saveIntegrationMetadata(IntegrationMetadata integrationMetadata) {
        integrationMetadata.setVersion(integrationMetadataRepository.findNextVersion(
                integrationMetadata.getSourceApplicationId(),
                integrationMetadata.getSourceApplicationIntegrationId()
        ));
        return integrationMetadataRepository.save(integrationMetadata);
    }

    public Collection<IntegrationMetadata> findLatestVersionForSourceApplication(
            Long sourceApplicationId
    ) {
        return integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(sourceApplicationId);
    }

    public Collection<IntegrationMetadata> findAllForSourceApplicationAndSourceApplicationIntegration(
            Long sourceApplicationId,
            String sourceApplicationIntegrationId
    ) {
        return integrationMetadataRepository
                .findAllBySourceApplicationIdAndSourceApplicationIntegrationId(
                        sourceApplicationId,
                        sourceApplicationIntegrationId
                );
    }

}
