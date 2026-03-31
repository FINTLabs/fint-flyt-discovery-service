package no.novari.flyt.discovery.service

import no.novari.flyt.discovery.service.model.entities.IntegrationMetadata
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext

@DataJpaTest(properties = ["spring.jpa.hibernate.ddl-auto=none"])
@DirtiesContext
class IntegrationMetadataRepositoryTest {
    @Autowired
    private lateinit var integrationMetadataRepository: IntegrationMetadataRepository

    @Test
    fun shouldReturnEmptyCollectionWhenSourceApplicationHasNoEntities() {
        integrationMetadataRepository.save(createIntegrationMetadata(2L, "TEST-1", 1L))

        val result = integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(1L)

        assertTrue(result.isEmpty())
    }

    @Test
    fun shouldReturnLatestVersionsForSourceApplication() {
        integrationMetadataRepository.saveAll(
            listOf(
                createIntegrationMetadata(1L, "TEST-1", 1L),
                createIntegrationMetadata(1L, "TEST-1", 3L),
                createIntegrationMetadata(1L, "TEST-1", 2L),
                createIntegrationMetadata(1L, "TEST-2", 1L),
                createIntegrationMetadata(2L, "TEST-1", 1L),
            ),
        )

        val result = integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(1L)
        val resultList = result.sortedBy { it.sourceApplicationIntegrationId }

        assertEquals(2, result.size)
        assertEquals("TEST-1", resultList[0].sourceApplicationIntegrationId)
        assertEquals(3L, resultList[0].version)
        assertEquals("TEST-2", resultList[1].sourceApplicationIntegrationId)
        assertEquals(1L, resultList[1].version)
    }

    @Test
    fun shouldReturnLatestVersionsForSourceApplications() {
        integrationMetadataRepository.saveAll(
            listOf(
                createIntegrationMetadata(1L, "TEST-1", 1L),
                createIntegrationMetadata(1L, "TEST-1", 2L),
                createIntegrationMetadata(1L, "TEST-2", 1L),
                createIntegrationMetadata(2L, "TEST-1", 1L),
                createIntegrationMetadata(2L, "TEST-1", 3L),
                createIntegrationMetadata(3L, "TEST-1", 5L),
            ),
        )

        val result =
            integrationMetadataRepository.findAllWithLatestVersionsForSourceApplications(listOf(1L, 2L))
        val resultList =
            result.sortedWith(
                compareBy<IntegrationMetadata> { it.sourceApplicationId }
                    .thenBy { it.sourceApplicationIntegrationId },
            )

        assertEquals(3, result.size)
        assertEquals(1L, resultList[0].sourceApplicationId)
        assertEquals("TEST-1", resultList[0].sourceApplicationIntegrationId)
        assertEquals(2L, resultList[0].version)
        assertEquals(1L, resultList[1].sourceApplicationId)
        assertEquals("TEST-2", resultList[1].sourceApplicationIntegrationId)
        assertEquals(1L, resultList[1].version)
        assertEquals(2L, resultList[2].sourceApplicationId)
        assertEquals("TEST-1", resultList[2].sourceApplicationIntegrationId)
        assertEquals(3L, resultList[2].version)
    }

    private fun createIntegrationMetadata(
        sourceApplicationId: Long,
        sourceApplicationIntegrationId: String,
        version: Long,
    ): IntegrationMetadata =
        IntegrationMetadata(
            sourceApplicationId = sourceApplicationId,
            sourceApplicationIntegrationId = sourceApplicationIntegrationId,
            version = version,
            integrationDisplayName = "displayName",
        )
}
