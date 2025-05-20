package no.fintlabs.kafka;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.IntegrationMetadataRepository;
import no.fintlabs.kafka.event.EventConsumerConfiguration;
import no.fintlabs.kafka.event.EventConsumerFactoryService;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.kafka.event.topic.EventTopicService;
import no.fintlabs.model.entities.IntegrationMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Slf4j
@Configuration
public class IntegrationMetadataEventConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, IntegrationMetadata> integrationMetadataEventConsumer(
            EventConsumerFactoryService eventConsumerFactoryService,
            IntegrationMetadataRepository integrationMetadataRepository,
            EventTopicService eventTopicService
    ) {
        EventTopicNameParameters eventTopicNameParameters = EventTopicNameParameters.builder()
                .eventName("integration-metadata-received")
                .build();
        eventTopicService.ensureTopic(eventTopicNameParameters, 0);

        return eventConsumerFactoryService.createRecordConsumerFactory(
                IntegrationMetadata.class,
                consumerRecord -> {
                    IntegrationMetadata integrationMetadata = consumerRecord.value();
                    if (!integrationMetadataRepository.existsBySourceApplicationIdAndAndSourceApplicationIntegrationIdAndVersion(
                            integrationMetadata.getSourceApplicationId(),
                            integrationMetadata.getSourceApplicationIntegrationId(),
                            integrationMetadata.getVersion()
                    )) {
                        integrationMetadataRepository.save(integrationMetadata);
                    } else {
                        log.warn("Ignored metadata with sourceApplicationId=" +
                                integrationMetadata.getSourceApplicationId() +
                                ", sourceApplicationIntegrationId=" +
                                integrationMetadata.getSourceApplicationIntegrationId() +
                                " and version=" +
                                integrationMetadata.getVersion() +
                                " because it already exists");
                    }
                },
                EventConsumerConfiguration
                        .builder()
                        .errorHandler(new DefaultErrorHandler())
                        .seekingOffsetResetOnAssignment(false)
                        .build()
        ).createContainer(
                eventTopicNameParameters
        );

    }

}
