package no.fintlabs.model.web;

import lombok.Data;
import no.fintlabs.validation.UniqueKeys;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class IntegrationMetadataPost {

    @NotNull
    private Long sourceApplicationId;

    @NotBlank
    private String sourceApplicationIntegrationId;

    private String sourceApplicationIntegrationUri;

    @NotBlank
    private String integrationDisplayName;

    @NotNull
    private Long version;

    @UniqueKeys
    private List<@NotNull @Valid InstanceElementMetadataPost> instanceElementMetadata;

}
