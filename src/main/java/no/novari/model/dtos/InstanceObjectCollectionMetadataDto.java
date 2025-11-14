package no.novari.model.dtos;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Builder
@EqualsAndHashCode
@Jacksonized
public class InstanceObjectCollectionMetadataDto {

    @NotBlank
    private final String displayName;

    @NotNull
    @Valid
    private final InstanceMetadataContentDto objectMetadata;

    @NotNull
    private final String key;

}
