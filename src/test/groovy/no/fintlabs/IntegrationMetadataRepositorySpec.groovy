package no.fintlabs

import no.fintlabs.model.IntegrationMetadata
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification


@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@DirtiesContext
class IntegrationMetadataRepositorySpec extends Specification {

    @Autowired
    IntegrationMetadataRepository integrationMetadataRepository

    private IntegrationMetadata createIntegrationMetadata(
            Long sourceApplicationId,
            String sourceApplicationIntegrationId,
            Integer version
    ) {
        return IntegrationMetadata
                .builder()
                .sourceApplicationId(sourceApplicationId)
                .sourceApplicationIntegrationId(sourceApplicationIntegrationId)
                .version(version)
                .integrationDisplayName("displayName")
                .build()
    }

    def "Given no entities for sourceApplication, when findAllWithLatestVersionsForSourceApplication is called, should return empty collection"() {
        given:
        integrationMetadataRepository.save(
                createIntegrationMetadata(2, "TEST-1", 1),

        )

        when:
        Collection<IntegrationMetadata> result = integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(1)

        then:
        result.isEmpty()
    }

    def "Given entities for source application, when findAllWithLatestVersionsForSourceApplication is called, should return entities of latest version related to the source application"() {
        given:
        integrationMetadataRepository.saveAll(List.of(
                createIntegrationMetadata(1, "TEST-1", 1),
                createIntegrationMetadata(1, "TEST-1", 3),
                createIntegrationMetadata(1, "TEST-1", 2),
                createIntegrationMetadata(1, "TEST-2", 1),
                createIntegrationMetadata(2, "TEST-1", 1),
        ))

        when:
        Collection<IntegrationMetadata> result = integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(1)

        then:
        result.size() == 2
        result.sort((im1, im2) -> im1.getSourceApplicationIntegrationId() <=> im2.getSourceApplicationIntegrationId())
        result[0].getSourceApplicationIntegrationId() == "TEST-1"
        result[0].getVersion() == 3
        result[1].getSourceApplicationIntegrationId() == "TEST-2"
        result[1].getVersion() == 1
    }

}
