package no.fintlabs.model.web;

import lombok.Data;

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

    private List<@NotNull @Valid InstanceElementMetadataPost> instanceElementMetadata;

}
