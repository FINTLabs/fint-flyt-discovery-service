package no.fintlabs

import no.fintlabs.model.InstanceElementMetadata
import no.fintlabs.model.IntegrationMetadata
import no.fintlabs.model.web.InstanceElementMetadataPost
import no.fintlabs.model.web.IntegrationMetadataPost
import spock.lang.Specification

class IntegrationMetadataMapperSpec extends Specification {

    IntegrationMetadataMapper integrationMetadataMapper
    IntegrationMetadataPost integrationMetadataPost
    IntegrationMetadata expectedIntegrationMetadata

    def setup() {

        integrationMetadataMapper = new IntegrationMetadataMapper()

        integrationMetadataPost = IntegrationMetadataPost
                .builder()
                .sourceApplicationId(1)
                .sourceApplicationIntegrationId("Test-1")
                .version(1)
                .integrationDisplayName("displayName")
                .instanceElementMetadata(List.of(
                        InstanceElementMetadataPost
                                .builder()
                                .displayName("DisplayName-1")
                                .children(List.of(
                                        InstanceElementMetadataPost
                                                .builder()
                                                .key("123")
                                                .type(InstanceElementMetadata.Type.DOUBLE)
                                                .displayName("DisplayName-1-1")
                                                .children(List.of())
                                                .build()
                                ))
                                .build()
                ))
                .build()

        expectedIntegrationMetadata = IntegrationMetadata
                .builder()
                .sourceApplicationId(1)
                .sourceApplicationIntegrationId("Test-1")
                .version(1)
                .integrationDisplayName("displayName")
                .instanceElementMetadata(List.of(
                        InstanceElementMetadata
                                .builder()
                                .displayName("DisplayName-1")
                                .children(List.of(
                                        InstanceElementMetadata
                                                .builder()
                                                .key("123")
                                                .type(InstanceElementMetadata.Type.DOUBLE)
                                                .displayName("DisplayName-1-1")
                                                .children(List.of())
                                                .build()
                                ))
                                .build()
                )
                )
                .build()
    }

    def 'Should map to integration Metadata'() {
        when:
        IntegrationMetadata integrationMetadata = integrationMetadataMapper.toIntegrationMetadata(integrationMetadataPost)

        then:
        integrationMetadata == expectedIntegrationMetadata
    }
}
