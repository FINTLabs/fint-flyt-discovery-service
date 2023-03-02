package no.fintlabs.mapping;

import no.fintlabs.model.dtos.IntegrationMetadataDto;
import no.fintlabs.model.entities.IntegrationMetadata;
import org.springframework.stereotype.Service;

@Service
public class IntegrationMetadataMappingService {

    private final InstanceMetadataContentMappingService instanceMetadataContentMappingService;

    public IntegrationMetadataMappingService(InstanceMetadataContentMappingService instanceMetadataContentMappingService) {
        this.instanceMetadataContentMappingService = instanceMetadataContentMappingService;
    }

    public IntegrationMetadata toEntity(IntegrationMetadataDto integrationMetadataDto) {
        return IntegrationMetadata
                .builder()
                .sourceApplicationId(integrationMetadataDto.getSourceApplicationId())
                .sourceApplicationIntegrationId(integrationMetadataDto.getSourceApplicationIntegrationId())
                .sourceApplicationIntegrationUri(integrationMetadataDto.getSourceApplicationIntegrationUri())
                .integrationDisplayName(integrationMetadataDto.getIntegrationDisplayName())
                .version(integrationMetadataDto.getVersion())
                .instanceMetadata(
                        instanceMetadataContentMappingService.toEntity(integrationMetadataDto.getInstanceMetadata())
                )
                .build();
    }

    public IntegrationMetadataDto toDto(IntegrationMetadata integrationMetadata) {
        return IntegrationMetadataDto
                .builder()
                .id(integrationMetadata.getId())
                .sourceApplicationId(integrationMetadata.getSourceApplicationId())
                .sourceApplicationIntegrationId(integrationMetadata.getSourceApplicationIntegrationId())
                .sourceApplicationIntegrationUri(integrationMetadata.getSourceApplicationIntegrationUri())
                .integrationDisplayName(integrationMetadata.getIntegrationDisplayName())
                .version(integrationMetadata.getVersion())
                .instanceMetadata(
                        instanceMetadataContentMappingService.toDto(integrationMetadata.getInstanceMetadata())
                )
                .build();
    }

}
