package no.novari.flyt.discovery.service.kafka

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import no.novari.flyt.discovery.service.IntegrationMetadataRepository
import no.novari.flyt.discovery.service.model.entities.IntegrationMetadata
import no.novari.kafka.consuming.ErrorHandlerConfiguration
import no.novari.kafka.consuming.ErrorHandlerFactory
import no.novari.kafka.consuming.ListenerConfiguration
import no.novari.kafka.consuming.ParameterizedListenerContainerFactoryService
import no.novari.kafka.producing.ParameterizedProducerRecord
import no.novari.kafka.producing.ParameterizedTemplateFactory
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Fastholder at katalogtjenesten kan overta `event.integration-metadata-received` fra
 * discovery-service uten datatap og uten duplikater, og at en tilbakerulling til discovery-service
 * fungerer selv om katalogtjenesten har skrevet rader i mellomtiden.
 *
 * En ny applikasjon får ny consumer group uten commitede offsets, og leser derfor topicen fra
 * begynnelsen. Det er tilsiktet: det er slik meldinger produsert i gapet mellom at den gamle
 * tjenesten stoppes og den nye starter, blir plukket opp. Prisen er at allerede behandlede
 * meldinger leses om igjen, og det er den prisen denne testen viser at vi kan betale.
 */
@SpringBootTest(
    properties = [
        "spring.kafka.bootstrap-servers=\${spring.embedded.kafka.brokers}",
        "spring.datasource.hikari.schema=public",
        "novari.kafka.topic.org-id=test-org-id",
        "novari.kafka.default-replicas=1",
        "fint.org-id=novari.no",
        "fint.flyt.authorization.sso.client-id=test-client-id",
        "fint.flyt.authorization.sso.client-secret=test-client-secret",
        "novari.flyt.web-resource-server.security.api.internal.authorized-org-id-role-pairs-json={}",
    ],
)
@EmbeddedKafka(partitions = 1, kraft = true)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class IntegrationMetadataEventIdempotencyIntegrationTest {
    @Autowired
    private lateinit var integrationMetadataRepository: IntegrationMetadataRepository

    @Autowired
    private lateinit var integrationMetadataEventHandler: IntegrationMetadataEventHandler

    @Autowired
    private lateinit var parameterizedListenerContainerFactoryService: ParameterizedListenerContainerFactoryService

    @Autowired
    private lateinit var parameterizedTemplateFactory: ParameterizedTemplateFactory

    @Autowired
    private lateinit var errorHandlerFactory: ErrorHandlerFactory

    @Autowired
    private lateinit var discoveryServiceConsumer: ConcurrentMessageListenerContainer<String, IntegrationMetadata>

    private lateinit var skippedEventAppender: ListAppender<ILoggingEvent>

    private var catalogServiceConsumer: ConcurrentMessageListenerContainer<String, IntegrationMetadata>? = null

    @BeforeEach
    fun setUp() {
        discoveryServiceConsumer.stop()
        catalogServiceConsumer = null
        integrationMetadataRepository.deleteAll()

        skippedEventAppender =
            ListAppender<ILoggingEvent>().apply { start() }
        handlerLogger().addAppender(skippedEventAppender)
    }

    @AfterEach
    fun tearDown() {
        catalogServiceConsumer?.stop()
        discoveryServiceConsumer.stop()
        handlerLogger().detachAppender(skippedEventAppender)
    }

    @Test
    fun `catalog service takes over without loss and rollback to discovery service reprocesses without duplicates`() {
        discoveryServiceConsumer.start()
        publish(version = 1)
        publish(version = 2)
        awaitStoredVersions(1, 2)

        discoveryServiceConsumer.stop()

        publish(version = 3)
        publish(version = 4)

        val catalogServiceConsumer = createAndRegisterCatalogConsumer()
        catalogServiceConsumer.start()

        awaitStoredVersions(1, 2, 3, 4)
        assertThat(skippedVersions()).containsExactlyInAnyOrder(1L, 2L)

        catalogServiceConsumer.stop()

        publish(version = 5)
        publish(version = 6)

        skippedEventAppender.list.clear()
        discoveryServiceConsumer.start()

        awaitStoredVersions(1, 2, 3, 4, 5, 6)
        assertThat(skippedVersions()).containsExactlyInAnyOrder(3L, 4L)
    }

    /**
     * Containeren opprettes utenfor Spring-konteksten, så ingenting stopper den automatisk.
     * Den registreres derfor for opprydding i [tearDown]: feiler testen mellom start og stop,
     * ville en kjørende consumer ellers blitt stående og forstyrret påfølgende tester.
     */
    private fun createAndRegisterCatalogConsumer(): ConcurrentMessageListenerContainer<String, IntegrationMetadata> =
        parameterizedListenerContainerFactoryService
            .createRecordListenerContainerFactory(
                IntegrationMetadata::class.java,
                integrationMetadataEventHandler,
                ListenerConfiguration
                    .stepBuilder()
                    .groupIdApplicationDefaultWithSuffix(CATALOG_SERVICE_GROUP_ID_SUFFIX)
                    .maxPollRecordsKafkaDefault()
                    .maxPollIntervalKafkaDefault()
                    .continueFromPreviousOffsetOnAssignment()
                    .build(),
                errorHandlerFactory.createErrorHandler(
                    ErrorHandlerConfiguration
                        .stepBuilder<IntegrationMetadata>()
                        .noRetries()
                        .skipFailedRecords()
                        .build(),
                ),
            ).createContainer(
                IntegrationMetadataEventConsumerConfiguration.integrationMetadataEventTopicNameParameters(),
            ).also { catalogServiceConsumer = it }

    private fun publish(version: Long) {
        parameterizedTemplateFactory
            .createTemplate(IntegrationMetadata::class.java)
            .send(
                ParameterizedProducerRecord
                    .builder<IntegrationMetadata>()
                    .topicNameParameters(
                        IntegrationMetadataEventConsumerConfiguration
                            .integrationMetadataEventTopicNameParameters(),
                    ).key("$SOURCE_APPLICATION_INTEGRATION_ID-$version")
                    .value(
                        IntegrationMetadata(
                            sourceApplicationId = SOURCE_APPLICATION_ID,
                            sourceApplicationIntegrationId = SOURCE_APPLICATION_INTEGRATION_ID,
                            integrationDisplayName = "Integrasjon $version",
                            version = version,
                        ),
                    ).build(),
            ).get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
    }

    private fun awaitStoredVersions(vararg expectedVersions: Long) {
        await()
            .atMost(TIMEOUT)
            .untilAsserted {
                assertThat(storedVersions()).containsExactlyInAnyOrder(*expectedVersions.toTypedArray())
            }
    }

    private fun storedVersions(): List<Long> =
        integrationMetadataRepository
            .findAll()
            .mapNotNull { it.version }

    private fun skippedVersions(): List<Long> =
        skippedEventAppender.list
            .filter { it.level == Level.WARN }
            .mapNotNull { event ->
                event.argumentArray
                    ?.lastOrNull()
                    ?.let { (it as? Long) }
            }

    private fun handlerLogger() =
        LoggerFactory.getLogger(IntegrationMetadataEventHandler::class.java) as ch.qos.logback.classic.Logger

    companion object {
        private const val CATALOG_SERVICE_GROUP_ID_SUFFIX = "-integration-catalog-service"
        private const val SOURCE_APPLICATION_ID = 1L
        private const val SOURCE_APPLICATION_INTEGRATION_ID = "TEST-1"
        private val TIMEOUT: Duration = Duration.ofSeconds(30)

        @Container
        @ServiceConnection
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17-alpine")
    }
}
