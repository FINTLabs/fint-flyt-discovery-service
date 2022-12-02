package no.fintlabs.model.web;

import lombok.Builder;
import lombok.Data;
import no.fintlabs.model.InstanceElementMetadata;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class InstanceElementMetadataPost {

    private String key;

    private InstanceElementMetadata.Type type;

    @NotBlank
    private String displayName;

    private List<@NotNull @Valid InstanceElementMetadataPost> children;

}
