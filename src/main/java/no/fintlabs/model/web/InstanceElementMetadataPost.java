package no.fintlabs.model.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fintlabs.model.InstanceElementMetadata;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstanceElementMetadataPost {

    private String key;

    private InstanceElementMetadata.Type type;

    @NotBlank
    private String displayName;

    private List<@NotNull @Valid InstanceElementMetadataPost> children;

}
