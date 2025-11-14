package no.novari.model.dtos;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Getter
@Builder
@EqualsAndHashCode
@Jacksonized
public class InstanceMetadataContentDto {

    public InstanceMetadataContentDto(
            Collection<InstanceValueMetadataDto> instanceValueMetadata,
            Collection<InstanceObjectCollectionMetadataDto> instanceObjectCollectionMetadata,
            Collection<InstanceMetadataCategoryDto> categories) {
        this.instanceValueMetadata = Optional.ofNullable(instanceValueMetadata).orElse(new ArrayList<>());
        this.instanceObjectCollectionMetadata = Optional.ofNullable(instanceObjectCollectionMetadata).orElse(new ArrayList<>());
        this.categories = Optional.ofNullable(categories).orElse(new ArrayList<>());
    }

    private final Collection<@Valid @NotNull InstanceValueMetadataDto> instanceValueMetadata;

    private final Collection<@Valid @NotNull InstanceObjectCollectionMetadataDto> instanceObjectCollectionMetadata;

    private final Collection<@Valid @NotNull InstanceMetadataCategoryDto> categories;

}
