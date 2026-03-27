package no.novari.flyt.discovery.service.mapping

import no.novari.flyt.discovery.service.model.dtos.InstanceValueMetadataDto
import no.novari.flyt.discovery.service.model.entities.InstanceValueMetadata
import org.springframework.stereotype.Service

@Service
class InstanceValueMetadataMappingService {
    fun toEntity(instanceValueMetadataDto: InstanceValueMetadataDto): InstanceValueMetadata {
        return InstanceValueMetadata(
            displayName = requireNotNull(instanceValueMetadataDto.displayName),
            type = requireNotNull(instanceValueMetadataDto.type),
            key = requireNotNull(instanceValueMetadataDto.key),
        )
    }

    fun toDto(instanceValueMetadata: InstanceValueMetadata): InstanceValueMetadataDto {
        return InstanceValueMetadataDto(
            displayName = requireNotNull(instanceValueMetadata.displayName),
            type = requireNotNull(instanceValueMetadata.type),
            key = requireNotNull(instanceValueMetadata.key),
        )
    }
}
