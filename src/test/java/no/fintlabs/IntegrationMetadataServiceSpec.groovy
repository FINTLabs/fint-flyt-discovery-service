package no.fintlabs

import no.fintlabs.model.fint.IntegrationMetadata
import spock.lang.Specification

class IntegrationMetadataServiceSpec extends Specification {

    IntegrationMetadataRepository integrationMetadataRepository
    IntegrationMetadataService integrationMetadataService

    def setup() {
        integrationMetadataRepository = Mock(IntegrationMetadataRepository.class)
        integrationMetadataService = new IntegrationMetadataService(integrationMetadataRepository)
    }

    def "Should save integration metadata with version returned from next version call to repository"() {
        given:
        IntegrationMetadata newIntegrationMetadata = IntegrationMetadata
                .builder()
                .sourceApplicationId("1")
                .sourceApplicationIntegrationId("TEST-1")
                .build()

        when:
        integrationMetadataService.saveIntegrationMetadata(newIntegrationMetadata)

        then:
        1 * integrationMetadataRepository.findNextVersion("1", "TEST-1") >> 2
        1 * integrationMetadataRepository.save(newIntegrationMetadata) >> { IntegrationMetadata integrationMetadata ->
            assert integrationMetadata.getSourceApplicationId() == "1"
            assert integrationMetadata.getSourceApplicationIntegrationId() == "TEST-1"
            assert integrationMetadata.getVersion() == 2
        }
        0 * _
    }

    def "Given repository finds entities for source application, when findLatestVersionPerSourceApplicationIntegrationIdForSourceApplication is called, should return entities from repository"() {
        given:
        IntegrationMetadata integrationMetadata1 =
                IntegrationMetadata
                        .builder()
                        .sourceApplicationId("1")
                        .sourceApplicationIntegrationId("TEST-1")
                        .version(1)
                        .build()
        IntegrationMetadata integrationMetadata2 =
                IntegrationMetadata
                        .builder()
                        .sourceApplicationId("1")
                        .sourceApplicationIntegrationId("TEST-2")
                        .version(4)
                        .build()
        IntegrationMetadata integrationMetadata3 =
                IntegrationMetadata
                        .builder()
                        .sourceApplicationId("1")
                        .sourceApplicationIntegrationId("TEST-3")
                        .version(2)
                        .build()

        when:
        Collection<IntegrationMetadata> integrationMetadata = integrationMetadataService
                .findLatestVersionForSourceApplication("1")

        then:
        1 * integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication("1") >> List.of(
                integrationMetadata1,
                integrationMetadata2,
                integrationMetadata3
        )
        integrationMetadata.size() == 3
        integrationMetadata.containsAll(integrationMetadata1, integrationMetadata2, integrationMetadata3)
    }

}
