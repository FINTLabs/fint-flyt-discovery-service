package no.fintlabs;

import no.fintlabs.model.fint.IntegrationMetadata;
import no.fintlabs.model.fint.IntegrationMetadataWrapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public IntegrationMetadataWrapper findLatestVersionPerSourceApplicationIntegrationIdForSourceApplication(
            String sourceApplicationId
    ) {
        Map<String, IntegrationMetadata> integrationMetadataPerSourceApplicationIntegrationId =
                integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(sourceApplicationId)
                        .stream()
                        .collect(Collectors.toMap(
                                IntegrationMetadata::getSourceApplicationIntegrationId,
                                Function.identity()
                        ));
        return new IntegrationMetadataWrapper(integrationMetadataPerSourceApplicationIntegrationId);
    }

}
