package no.fintlabs.model.dtos;

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
public class InstanceObjectCollectionMetadataDto {

    @NotBlank
    private final String displayName;

    @NotNull
    @Valid
    private final InstanceMetadataContentDto objectMetadata;

    @NotNull
    private String key;

}
