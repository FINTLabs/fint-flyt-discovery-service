package no.novari.flyt.discovery.service.model.dtos;

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
public class InstanceMetadataCategoryDto {

    @NotBlank
    private final String displayName;

    @NotNull
    @Valid
    private final InstanceMetadataContentDto content;

}
