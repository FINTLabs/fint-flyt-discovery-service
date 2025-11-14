package no.novari.mapping;

import no.novari.model.dtos.InstanceValueMetadataDto;
import no.novari.model.entities.InstanceValueMetadata;
import org.springframework.stereotype.Service;

@Service
public class InstanceValueMetadataMappingService {

    public InstanceValueMetadata toEntity(InstanceValueMetadataDto instanceValueMetadataDto) {
        return InstanceValueMetadata
                .builder()
                .displayName(instanceValueMetadataDto.getDisplayName())
                .type(instanceValueMetadataDto.getType())
                .key(instanceValueMetadataDto.getKey())
                .build();
    }

    public InstanceValueMetadataDto toDto(InstanceValueMetadata instanceValueMetadata) {
        return InstanceValueMetadataDto
                .builder()
                .displayName(instanceValueMetadata.getDisplayName())
                .type(instanceValueMetadata.getType())
                .key(instanceValueMetadata.getKey())
                .build();
    }

}
