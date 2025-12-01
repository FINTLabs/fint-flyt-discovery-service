package no.novari.flyt.discovery.service.model.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@EqualsAndHashCode
@Jacksonized
public class IntegrationMetadataDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final Long id;

    @NotNull
    private final Long sourceApplicationId;

    @NotBlank
    private final String sourceApplicationIntegrationId;

    private final String sourceApplicationIntegrationUri;

    @NotBlank
    private final String integrationDisplayName;

    @NotNull
    private final Long version;

    @Valid
    private final InstanceMetadataContentDto instanceMetadata;

}
