package no.novari;

import no.novari.model.entities.IntegrationMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@DirtiesContext
class IntegrationMetadataRepositoryTest {

    @Autowired
    IntegrationMetadataRepository integrationMetadataRepository;

    private IntegrationMetadata createIntegrationMetadata(
            Long sourceApplicationId,
            String sourceApplicationIntegrationId,
            Long version
    ) {
        return IntegrationMetadata
                .builder()
                .sourceApplicationId(sourceApplicationId)
                .sourceApplicationIntegrationId(sourceApplicationIntegrationId)
                .version(version)
                .integrationDisplayName("displayName")
                .build();
    }

    @Test
    void givenNoEntitiesForSourceApplication_whenFindAllWithLatestVersionsForSourceApplicationIsCalled_shouldReturnEmptyCollection() {
        integrationMetadataRepository.save(
                createIntegrationMetadata(2L, "TEST-1", 1L)
        );

        Collection<IntegrationMetadata> result = integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenEntitiesForSourceApplication_whenFindAllWithLatestVersionsForSourceApplicationIsCalled_shouldReturnEntitiesOfLatestVersionRelatedToTheSourceApplication() {
        integrationMetadataRepository.saveAll(Arrays.asList(
                createIntegrationMetadata(1L, "TEST-1", 1L),
                createIntegrationMetadata(1L, "TEST-1", 3L),
                createIntegrationMetadata(1L, "TEST-1", 2L),
                createIntegrationMetadata(1L, "TEST-2", 1L),
                createIntegrationMetadata(2L, "TEST-1", 1L)
        ));

        Collection<IntegrationMetadata> result = integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(1L);

        assertEquals(2, result.size());

        List<IntegrationMetadata> resultList = new ArrayList<>(result);
        resultList.sort(Comparator.comparing(IntegrationMetadata::getSourceApplicationIntegrationId));

        assertEquals("TEST-1", resultList.get(0).getSourceApplicationIntegrationId());
        assertEquals(Long.valueOf(3), resultList.get(0).getVersion());

        assertEquals("TEST-2", resultList.get(1).getSourceApplicationIntegrationId());
        assertEquals(Long.valueOf(1), resultList.get(1).getVersion());
    }

}
