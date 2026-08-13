package no.novari.flyt.discovery.service.kafka

import io.github.oshai.kotlinlogging.KotlinLogging
import no.novari.flyt.discovery.service.IntegrationMetadataRepository
import no.novari.flyt.discovery.service.model.entities.IntegrationMetadata
import no.novari.kafka.consuming.ErrorHandlerConfiguration
import no.novari.kafka.consuming.ErrorHandlerFactory
import no.novari.kafka.consuming.ListenerConfiguration
import no.novari.kafka.consuming.ParameterizedListenerContainerFactoryService
import no.novari.kafka.topic.EventTopicService
import no.novari.kafka.topic.configuration.EventCleanupFrequency
import no.novari.kafka.topic.configuration.EventTopicConfiguration
import no.novari.kafka.topic.name.EventTopicNameParameters
import no.novari.kafka.topic.name.TopicNamePrefixParameters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import java.time.Duration

@Configuration
class IntegrationMetadataEventConsumerConfiguration {
    @Bean
    fun integrationMetadataEventConsumer(
        parameterizedListenerContainerFactoryService: ParameterizedListenerContainerFactoryService,
        integrationMetadataRepository: IntegrationMetadataRepository,
        eventTopicService: EventTopicService,
        errorHandlerFactory: ErrorHandlerFactory,
    ): ConcurrentMessageListenerContainer<String, IntegrationMetadata> {
        val eventTopicNameParameters =
            EventTopicNameParameters
                .builder()
                .eventName("integration-metadata-received")
                .topicNamePrefixParameters(
                    TopicNamePrefixParameters
                        .stepBuilder()
                        .orgIdApplicationDefault()
                        .domainContextApplicationDefault()
                        .build(),
                ).build()

        eventTopicService.createOrModifyTopic(
            eventTopicNameParameters,
            EventTopicConfiguration
                .stepBuilder()
                .partitions(PARTITIONS)
                .retentionTime(RETENTION_TIME)
                .cleanupFrequency(EventCleanupFrequency.NORMAL)
                .build(),
        )

        return parameterizedListenerContainerFactoryService
            .createRecordListenerContainerFactory(
                IntegrationMetadata::class.java,
                { consumerRecord ->
                    val integrationMetadata = consumerRecord.value()
                    val sourceApplicationId = requireNotNull(integrationMetadata.sourceApplicationId)
                    val sourceApplicationIntegrationId =
                        requireNotNull(integrationMetadata.sourceApplicationIntegrationId)
                    val version = requireNotNull(integrationMetadata.version)
                    val exists =
                        integrationMetadataRepository
                            .existsBySourceApplicationIdAndSourceApplicationIntegrationIdAndVersion(
                                sourceApplicationId,
                                sourceApplicationIntegrationId,
                                version,
                            )

                    if (!exists) {
                        integrationMetadataRepository.save(integrationMetadata)
                    } else {
                        log.atWarn {
                            message =
                                "Ignored metadata with sourceApplicationId={}, " +
                                "sourceApplicationIntegrationId={} and version={} because it already exists"
                            arguments = arrayOf(sourceApplicationId, sourceApplicationIntegrationId, version)
                        }
                    }
                },
                ListenerConfiguration
                    .stepBuilder()
                    .groupIdApplicationDefault()
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
            ).createContainer(eventTopicNameParameters)
    }

    companion object {
        private val log = KotlinLogging.logger {}
        private const val PARTITIONS = 1
        private val RETENTION_TIME: Duration = Duration.ofDays(7)
    }
}
