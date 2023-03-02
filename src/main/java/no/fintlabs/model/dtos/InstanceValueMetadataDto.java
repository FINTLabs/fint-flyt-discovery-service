package no.fintlabs.model.dtos;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import no.fintlabs.model.entities.InstanceValueMetadata;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@EqualsAndHashCode
@Jacksonized
public class InstanceValueMetadataDto {

    @NotBlank
    private final String displayName;

    @NotNull
    private final InstanceValueMetadata.Type type;

    @NotNull
    private final String key;

}
