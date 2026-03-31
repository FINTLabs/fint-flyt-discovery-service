package no.novari.flyt.discovery.service.mapping

import no.novari.flyt.discovery.service.model.dtos.IntegrationMetadataDto
import no.novari.flyt.discovery.service.model.entities.IntegrationMetadata
import org.springframework.stereotype.Service

@Service
class IntegrationMetadataMappingService(
    private val instanceMetadataContentMappingService: InstanceMetadataContentMappingService,
) {
    fun toEntity(integrationMetadataDto: IntegrationMetadataDto): IntegrationMetadata =
        IntegrationMetadata(
            sourceApplicationId = requireNotNull(integrationMetadataDto.sourceApplicationId),
            sourceApplicationIntegrationId = requireNotNull(integrationMetadataDto.sourceApplicationIntegrationId),
            sourceApplicationIntegrationUri = integrationMetadataDto.sourceApplicationIntegrationUri,
            integrationDisplayName = requireNotNull(integrationMetadataDto.integrationDisplayName),
            version = requireNotNull(integrationMetadataDto.version),
            instanceMetadata =
                integrationMetadataDto.instanceMetadata?.let(instanceMetadataContentMappingService::toEntity),
        )

    fun toDto(integrationMetadata: IntegrationMetadata): IntegrationMetadataDto =
        IntegrationMetadataDto(
            id = integrationMetadata.id,
            sourceApplicationId = requireNotNull(integrationMetadata.sourceApplicationId),
            sourceApplicationIntegrationId = requireNotNull(integrationMetadata.sourceApplicationIntegrationId),
            sourceApplicationIntegrationUri = integrationMetadata.sourceApplicationIntegrationUri,
            integrationDisplayName = requireNotNull(integrationMetadata.integrationDisplayName),
            version = requireNotNull(integrationMetadata.version),
            instanceMetadata =
                integrationMetadata.instanceMetadata?.let(instanceMetadataContentMappingService::toDto),
        )
}
