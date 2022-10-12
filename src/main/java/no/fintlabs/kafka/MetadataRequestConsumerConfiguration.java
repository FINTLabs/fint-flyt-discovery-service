package no.fintlabs.kafka;

import no.fintlabs.IntegrationMetadataRepository;
import no.fintlabs.kafka.common.topic.TopicCleanupPolicyParameters;
import no.fintlabs.kafka.requestreply.ReplyProducerRecord;
import no.fintlabs.kafka.requestreply.RequestConsumerFactoryService;
import no.fintlabs.kafka.requestreply.topic.RequestTopicNameParameters;
import no.fintlabs.kafka.requestreply.topic.RequestTopicService;
import no.fintlabs.model.fint.IntegrationMetadata;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class MetadataRequestConsumerConfiguration {

    @Bean
    ConcurrentMessageListenerContainer<String, Long>
    metadataByMetadataIdRequestConsumer(
            RequestTopicService requestTopicService,
            RequestConsumerFactoryService requestConsumerFactoryService,
            IntegrationMetadataRepository integrationMetadataRepository
    ) {
        RequestTopicNameParameters requestTopicNameParameters = RequestTopicNameParameters
                .builder()
                .resource("metadata")
                .parameterName("metadata-id")
                .build();
        requestTopicService
                .ensureTopic(requestTopicNameParameters, 0, TopicCleanupPolicyParameters.builder().build());

        return requestConsumerFactoryService.createFactory(
                Long.class,
                IntegrationMetadata.class,
                (ConsumerRecord<String, Long> consumerRecord) -> ReplyProducerRecord
                        .<IntegrationMetadata>builder()
                        .value(integrationMetadataRepository.findById(consumerRecord.value()).orElse(null))
                        .build(),
                new CommonLoggingErrorHandler()
        ).createContainer(requestTopicNameParameters);
    }
}
