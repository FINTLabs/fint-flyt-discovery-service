package no.novari.kafka;

import no.novari.IntegrationMetadataRepository;
import no.novari.IntegrationMetadataService;
import no.novari.kafka.consuming.ErrorHandlerConfiguration;
import no.novari.kafka.consuming.ErrorHandlerFactory;
import no.novari.kafka.requestreply.ReplyProducerRecord;
import no.novari.kafka.requestreply.RequestListenerConfiguration;
import no.novari.kafka.requestreply.RequestListenerContainerFactory;
import no.novari.kafka.requestreply.topic.RequestTopicService;
import no.novari.kafka.requestreply.topic.configuration.RequestTopicConfiguration;
import no.novari.kafka.requestreply.topic.name.RequestTopicNameParameters;
import no.novari.kafka.topic.name.TopicNamePrefixParameters;
import no.novari.model.dtos.InstanceMetadataContentDto;
import no.novari.model.entities.IntegrationMetadata;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.time.Duration;

@Configuration
public class MetadataRequestConsumerConfiguration {


    @Bean
    ConcurrentMessageListenerContainer<String, Long>
    metadataByMetadataIdRequestConsumer(
            RequestTopicService requestTopicService,
            RequestListenerContainerFactory requestListenerFactoryService,
            IntegrationMetadataRepository integrationMetadataRepository,
            ErrorHandlerFactory errorHandlerFactory
    ) {
        RequestTopicNameParameters requestTopicNameParameters = RequestTopicNameParameters
                .builder()
                .topicNamePrefixParameters(
                        TopicNamePrefixParameters
                                .builder()
                                .orgIdApplicationDefault()
                                .domainContextApplicationDefault()
                                .build()
                )
                .resourceName("metadata")
                .parameterName("metadata-id")
                .build();
        requestTopicService
                .createOrModifyTopic(
                        requestTopicNameParameters,
                        RequestTopicConfiguration
                                .builder()
                                .retentionTime(Duration.ofMinutes(5))
                                .build()
                );

        return requestListenerFactoryService.createRecordConsumerFactory(
                Long.class,
                IntegrationMetadata.class,
                (ConsumerRecord<String, Long> consumerRecord) -> ReplyProducerRecord
                        .<IntegrationMetadata>builder()
                        .value(integrationMetadataRepository.findById(consumerRecord.value()).orElse(null))
                        .build(),
                RequestListenerConfiguration
                        .stepBuilder(Long.class)
                        .maxPollRecordsKafkaDefault()
                        .maxPollIntervalKafkaDefault()
                        .build(),
                errorHandlerFactory.createErrorHandler(
                        ErrorHandlerConfiguration
                                .stepBuilder()
                                .noRetries()
                                .skipFailedRecords()
                                .build()
                )
        ).createContainer(requestTopicNameParameters);
    }

    @Bean
    ConcurrentMessageListenerContainer<String, Long>
    instanceMetadataByMetadataIdRequestConsumer(
            RequestTopicService requestTopicService,
            RequestListenerContainerFactory requestListenerFactoryService,
            IntegrationMetadataService integrationMetadataService,
            ErrorHandlerFactory errorHandlerFactory
    ) {
        RequestTopicNameParameters requestTopicNameParameters = RequestTopicNameParameters
                .builder()
                .resourceName("instance-metadata")
                .parameterName("metadata-id")
                .topicNamePrefixParameters(
                        TopicNamePrefixParameters
                                .builder()
                                .orgIdApplicationDefault()
                                .domainContextApplicationDefault()
                                .build()
                )
                .build();
        requestTopicService
                .createOrModifyTopic(
                        requestTopicNameParameters,
                        RequestTopicConfiguration
                                .builder()
                                .retentionTime(Duration.ZERO)
                                .build()
                );

        return requestListenerFactoryService.createRecordConsumerFactory(
                Long.class,
                InstanceMetadataContentDto.class,
                (ConsumerRecord<String, Long> consumerRecord) -> ReplyProducerRecord
                        .<InstanceMetadataContentDto>builder()
                        .value(
                                integrationMetadataService
                                        .getInstanceMetadataById(consumerRecord.value())
                                        .orElse(null)
                        )
                        .build(),
                RequestListenerConfiguration
                        .stepBuilder(Long.class)
                        .maxPollRecordsKafkaDefault()
                        .maxPollIntervalKafkaDefault()
                        .build(),
                errorHandlerFactory.createErrorHandler(
                        ErrorHandlerConfiguration
                                .stepBuilder()
                                .noRetries()
                                .skipFailedRecords()
                                .build()
                )
        ).createContainer(requestTopicNameParameters);
    }

}
