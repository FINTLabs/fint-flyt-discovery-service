package no.novari.kafka;

import lombok.extern.slf4j.Slf4j;
import no.novari.IntegrationMetadataRepository;
import no.novari.kafka.consuming.ErrorHandlerConfiguration;
import no.novari.kafka.consuming.ErrorHandlerFactory;
import no.novari.kafka.consuming.ListenerConfiguration;
import no.novari.kafka.consuming.ParameterizedListenerContainerFactoryService;
import no.novari.kafka.topic.EventTopicService;
import no.novari.kafka.topic.configuration.EventCleanupFrequency;
import no.novari.kafka.topic.configuration.EventTopicConfiguration;
import no.novari.kafka.topic.name.EventTopicNameParameters;
import no.novari.kafka.topic.name.TopicNamePrefixParameters;
import no.novari.model.entities.IntegrationMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.time.Duration;

@Slf4j
@Configuration
public class IntegrationMetadataEventConsumerConfiguration {

    private static final int PARTITIONS = 1;
    private static final int RETENTION_TIME_IN_DAYS = 7;

    @Bean
    public ConcurrentMessageListenerContainer<String, IntegrationMetadata> integrationMetadataEventConsumer(
            ParameterizedListenerContainerFactoryService parameterizedListenerContainerFactoryService,
            IntegrationMetadataRepository integrationMetadataRepository,
            EventTopicService eventTopicService,
            ErrorHandlerFactory errorHandlerFactory
    ) {
        EventTopicNameParameters eventTopicNameParameters = EventTopicNameParameters.builder()
                .eventName("integration-metadata-received")
                .topicNamePrefixParameters(
                        TopicNamePrefixParameters
                                .stepBuilder()
                                .orgIdApplicationDefault()
                                .domainContextApplicationDefault()
                                .build()
                )
                .build();
        eventTopicService.createOrModifyTopic(
                eventTopicNameParameters,
                EventTopicConfiguration.stepBuilder()
                        .partitions(PARTITIONS)
                        .retentionTime(Duration.ofDays(RETENTION_TIME_IN_DAYS))
                        .cleanupFrequency(EventCleanupFrequency.NORMAL)
                        .build()
        );

        return parameterizedListenerContainerFactoryService.createRecordListenerContainerFactory(
                IntegrationMetadata.class,
                consumerRecord -> {
                    IntegrationMetadata integrationMetadata = consumerRecord.value();
                    if (!integrationMetadataRepository.existsBySourceApplicationIdAndSourceApplicationIntegrationIdAndVersion(
                            integrationMetadata.getSourceApplicationId(),
                            integrationMetadata.getSourceApplicationIntegrationId(),
                            integrationMetadata.getVersion()
                    )) {
                        integrationMetadataRepository.save(integrationMetadata);
                    } else {
                        log.warn(
                                "Ignored metadata with sourceApplicationId={}, " +
                                        "sourceApplicationIntegrationId={} and version={} because it already exists",
                                integrationMetadata.getSourceApplicationId(),
                                integrationMetadata.getSourceApplicationIntegrationId(),
                                integrationMetadata.getVersion()
                        );
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
                                .stepBuilder()
                                .noRetries()
                                .skipFailedRecords()
                                .build()
                )
        ).createContainer(
                eventTopicNameParameters
        );

    }

}
