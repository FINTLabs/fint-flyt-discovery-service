package no.novari.flyt.discovery.service.model.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class IntegrationMetadataDto(
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: Long? = null,
    @field:NotNull
    var sourceApplicationId: Long? = null,
    @field:NotBlank
    val sourceApplicationIntegrationId: String? = null,
    val sourceApplicationIntegrationUri: String? = null,
    @field:NotBlank
    val integrationDisplayName: String? = null,
    @field:NotNull
    var version: Long? = null,
    @field:Valid
    val instanceMetadata: InstanceMetadataContentDto? = null,
)
