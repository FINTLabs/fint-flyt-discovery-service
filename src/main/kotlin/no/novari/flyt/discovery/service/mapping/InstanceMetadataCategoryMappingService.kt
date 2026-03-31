package no.novari.flyt.discovery.service.mapping

import no.novari.flyt.discovery.service.model.dtos.InstanceMetadataCategoryDto
import no.novari.flyt.discovery.service.model.entities.InstanceMetadataCategory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class InstanceMetadataCategoryMappingService(
    @param:Lazy private val instanceMetadataContentMappingService: InstanceMetadataContentMappingService,
) {
    fun toEntity(instanceMetadataCategoryDto: InstanceMetadataCategoryDto): InstanceMetadataCategory =
        InstanceMetadataCategory(
            displayName = requireNotNull(instanceMetadataCategoryDto.displayName),
            content =
                instanceMetadataContentMappingService.toEntity(
                    requireNotNull(instanceMetadataCategoryDto.content),
                ),
        )

    fun toDto(instanceMetadataCategory: InstanceMetadataCategory): InstanceMetadataCategoryDto =
        InstanceMetadataCategoryDto(
            displayName = requireNotNull(instanceMetadataCategory.displayName),
            content = instanceMetadataContentMappingService.toDto(requireNotNull(instanceMetadataCategory.content)),
        )
}
