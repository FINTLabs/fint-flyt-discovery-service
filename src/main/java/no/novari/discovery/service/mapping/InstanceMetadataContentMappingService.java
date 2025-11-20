package no.novari.discovery.service.mapping;

import no.novari.discovery.service.model.dtos.InstanceMetadataContentDto;
import no.novari.discovery.service.model.entities.InstanceMetadataContent;
import org.springframework.stereotype.Service;

@Service
public class InstanceMetadataContentMappingService {

    private final InstanceValueMetadataMappingService instanceValueMetadataMappingService;
    private final InstanceObjectCollectionMetadataMappingService instanceObjectCollectionMetadataMappingService;
    private final InstanceMetadataCategoryMappingService instanceMetadataCategoryMappingService;

    public InstanceMetadataContentMappingService(
            InstanceValueMetadataMappingService instanceValueMetadataMappingService,
            InstanceObjectCollectionMetadataMappingService instanceObjectCollectionMetadataMappingService,
            InstanceMetadataCategoryMappingService instanceMetadataCategoryMappingService
    ) {
        this.instanceValueMetadataMappingService = instanceValueMetadataMappingService;
        this.instanceObjectCollectionMetadataMappingService = instanceObjectCollectionMetadataMappingService;
        this.instanceMetadataCategoryMappingService = instanceMetadataCategoryMappingService;
    }

    public InstanceMetadataContent toEntity(InstanceMetadataContentDto instanceMetadataContentDto) {
        return InstanceMetadataContent
                .builder()
                .instanceValueMetadata(
                        instanceMetadataContentDto.getInstanceValueMetadata()
                                .stream()
                                .map(instanceValueMetadataMappingService::toEntity)
                                .toList()
                )
                .instanceObjectCollectionMetadata(
                        instanceMetadataContentDto.getInstanceObjectCollectionMetadata()
                                .stream()
                                .map(instanceObjectCollectionMetadataMappingService::toEntity)
                                .toList()
                )
                .categories(
                        instanceMetadataContentDto.getCategories()
                                .stream()
                                .map(instanceMetadataCategoryMappingService::toEntity)
                                .toList()
                )
                .build();
    }

    public InstanceMetadataContentDto toDto(InstanceMetadataContent instanceMetadataContent) {
        return InstanceMetadataContentDto
                .builder()
                .instanceValueMetadata(
                        instanceMetadataContent.getInstanceValueMetadata()
                                .stream()
                                .map(instanceValueMetadataMappingService::toDto)
                                .toList()
                )
                .instanceObjectCollectionMetadata(
                        instanceMetadataContent.getInstanceObjectCollectionMetadata()
                                .stream()
                                .map(instanceObjectCollectionMetadataMappingService::toDto)
                                .toList()
                )
                .categories(
                        instanceMetadataContent.getCategories()
                                .stream()
                                .map(instanceMetadataCategoryMappingService::toDto)
                                .toList()
                )
                .build();
    }

}
