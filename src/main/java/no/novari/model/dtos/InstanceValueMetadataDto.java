package no.novari.model.dtos;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import no.novari.model.entities.InstanceValueMetadata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
