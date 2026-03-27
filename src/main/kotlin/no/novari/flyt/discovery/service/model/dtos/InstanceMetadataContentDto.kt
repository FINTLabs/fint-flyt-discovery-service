package no.novari.flyt.discovery.service.model.dtos

import jakarta.validation.Valid

data class InstanceMetadataContentDto(
    @field:Valid
    val instanceValueMetadata: List<@Valid InstanceValueMetadataDto> = emptyList(),
    @field:Valid
    val instanceObjectCollectionMetadata: List<@Valid InstanceObjectCollectionMetadataDto> = emptyList(),
    @field:Valid
    val categories: List<@Valid InstanceMetadataCategoryDto> = emptyList(),
)
