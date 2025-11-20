package no.novari.flyt.discovery.service.mapping;

import no.novari.flyt.discovery.service.model.dtos.InstanceMetadataCategoryDto;
import no.novari.flyt.discovery.service.model.entities.InstanceMetadataCategory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class InstanceMetadataCategoryMappingService {

    private final InstanceMetadataContentMappingService instanceMetadataContentMappingService;

    public InstanceMetadataCategoryMappingService(
            @Lazy InstanceMetadataContentMappingService instanceMetadataContentMappingService
    ) {
        this.instanceMetadataContentMappingService = instanceMetadataContentMappingService;
    }

    public InstanceMetadataCategory toEntity(InstanceMetadataCategoryDto instanceMetadataCategoryDto) {
        return InstanceMetadataCategory
                .builder()
                .displayName(instanceMetadataCategoryDto.getDisplayName())
                .content(instanceMetadataContentMappingService.toEntity(instanceMetadataCategoryDto.getContent()))
                .build();
    }

    public InstanceMetadataCategoryDto toDto(InstanceMetadataCategory instanceMetadataCategory) {
        return InstanceMetadataCategoryDto
                .builder()
                .displayName(instanceMetadataCategory.getDisplayName())
                .content(instanceMetadataContentMappingService.toDto(instanceMetadataCategory.getContent()))
                .build();
    }

}
