package no.novari.mapping;

import no.novari.model.dtos.InstanceObjectCollectionMetadataDto;
import no.novari.model.entities.InstanceObjectCollectionMetadata;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class InstanceObjectCollectionMetadataMappingService {

    private final InstanceMetadataContentMappingService instanceMetadataContentMappingService;

    public InstanceObjectCollectionMetadataMappingService(
            @Lazy InstanceMetadataContentMappingService instanceMetadataContentMappingService
    ) {
        this.instanceMetadataContentMappingService = instanceMetadataContentMappingService;
    }

    public InstanceObjectCollectionMetadata toEntity(InstanceObjectCollectionMetadataDto instanceObjectCollectionMetadataDto) {
        return InstanceObjectCollectionMetadata
                .builder()
                .displayName(instanceObjectCollectionMetadataDto.getDisplayName())
                .objectMetadata(instanceMetadataContentMappingService.toEntity(
                        instanceObjectCollectionMetadataDto.getObjectMetadata()
                ))
                .key(instanceObjectCollectionMetadataDto.getKey())
                .build();
    }

    public InstanceObjectCollectionMetadataDto toDto(InstanceObjectCollectionMetadata instanceObjectCollectionMetadata) {
        return InstanceObjectCollectionMetadataDto
                .builder()
                .displayName(instanceObjectCollectionMetadata.getDisplayName())
                .objectMetadata(instanceMetadataContentMappingService.toDto(
                        instanceObjectCollectionMetadata.getObjectMetadata()
                ))
                .key(instanceObjectCollectionMetadata.getKey())
                .build();
    }

}
