package no.fintlabs;

import no.fintlabs.kafka.event.EventConsumerConfiguration;
import no.fintlabs.kafka.event.EventConsumerFactoryService;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.model.fint.IntegrationMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class IntegrationMetadataEventConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, IntegrationMetadata> integrationMetadataEventConsumer(
            EventConsumerFactoryService eventConsumerFactoryService,
            IntegrationMetadataService integrationMetadataService
    ) {
        return eventConsumerFactoryService.createFactory(
                IntegrationMetadata.class,
                consumerRecord -> integrationMetadataService.saveIntegrationMetadata(consumerRecord.value()),
                EventConsumerConfiguration
                        .builder()
                        .errorHandler(new CommonLoggingErrorHandler())
                        .seekingOffsetResetOnAssignment(false)
                        .build()
        ).createContainer(
                EventTopicNameParameters.builder()
                        .eventName("integration-metadata-received")
                        .build()
        );

    }

}
