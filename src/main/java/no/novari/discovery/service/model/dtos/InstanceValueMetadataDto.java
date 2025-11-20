package no.novari.discovery.service.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import no.novari.discovery.service.model.entities.InstanceValueMetadata;

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
