package no.fintlabs;

import no.fintlabs.model.InstanceElementMetadata;
import no.fintlabs.model.IntegrationMetadata;
import no.fintlabs.model.web.InstanceElementMetadataPost;
import no.fintlabs.model.web.IntegrationMetadataPost;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntegrationMetadataMapper {

    public IntegrationMetadata toIntegrationMetadata(IntegrationMetadataPost integrationMetadataPost) {
        return IntegrationMetadata
                .builder()
                .sourceApplicationId(integrationMetadataPost.getSourceApplicationId())
                .sourceApplicationIntegrationId(integrationMetadataPost.getSourceApplicationIntegrationId())
                .sourceApplicationIntegrationUri(integrationMetadataPost.getSourceApplicationIntegrationUri())
                .integrationDisplayName(integrationMetadataPost.getIntegrationDisplayName())
                .version(integrationMetadataPost.getVersion())
                .instanceElementMetadata(
                        toInstanceElementMetadata(integrationMetadataPost.getInstanceElementMetadata())
                )
                .build();
    }

    private List<InstanceElementMetadata> toInstanceElementMetadata(
            List<InstanceElementMetadataPost> instanceElementMetadataPost
    ) {
        return instanceElementMetadataPost.stream().map(this::toInstanceElementMetadata).toList();
    }

    private InstanceElementMetadata toInstanceElementMetadata(
            InstanceElementMetadataPost instanceElementMetadataPost
    ) {
        return InstanceElementMetadata
                .builder()
                .key(instanceElementMetadataPost.getKey())
                .type(instanceElementMetadataPost.getType())
                .displayName(instanceElementMetadataPost.getDisplayName())
                .children(toInstanceElementMetadata(instanceElementMetadataPost.getChildren()))
                .build();
    }
}
