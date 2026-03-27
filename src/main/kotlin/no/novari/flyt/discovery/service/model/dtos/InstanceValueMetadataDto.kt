package no.novari.flyt.discovery.service.model.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import no.novari.flyt.discovery.service.model.entities.InstanceValueMetadata

data class InstanceValueMetadataDto(
    @field:NotBlank
    val displayName: String? = null,
    @field:NotNull
    var type: InstanceValueMetadata.Type? = null,
    @field:NotBlank
    val key: String? = null,
)
