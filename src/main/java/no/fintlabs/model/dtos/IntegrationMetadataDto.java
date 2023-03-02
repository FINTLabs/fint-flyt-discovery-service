package no.fintlabs.model.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
