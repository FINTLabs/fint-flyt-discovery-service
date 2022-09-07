package no.fintlabs.model.fint;

import lombok.Data;

import java.util.Map;

@Data
public class IntegrationMetadataWrapper {
    private final Map<String, IntegrationMetadata> integrationMetadataPerSourceApplicationIntegrationId;
}
