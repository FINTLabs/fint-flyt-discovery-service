package no.novari.flyt.discovery.service.kafka;

import no.novari.flyt.discovery.service.IntegrationMetadataRepository;
import no.novari.flyt.discovery.service.IntegrationMetadataService;
import no.novari.flyt.discovery.service.model.dtos.InstanceMetadataContentDto;
import no.novari.flyt.discovery.service.model.entities.IntegrationMetadata;
import no.novari.kafka.consuming.ErrorHandlerConfiguration;
import no.novari.kafka.consuming.ErrorHandlerFactory;
import no.novari.kafka.requestreply.ReplyProducerRecord;
import no.novari.kafka.requestreply.RequestListenerConfiguration;
import no.novari.kafka.requestreply.RequestListenerContainerFactory;
import no.novari.kafka.requestreply.topic.RequestTopicService;
import no.novari.kafka.requestreply.topic.configuration.RequestTopicConfiguration;
import no.novari.kafka.requestreply.topic.name.RequestTopicNameParameters;
import no.novari.kafka.topic.name.TopicNamePrefixParameters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.time.Duration;

@Configuration
public class MetadataRequestConsumerConfiguration {

    public static final Duration RETENTION_TIME_METADATA_TOPIC = Duration.ofMinutes(10);
    public static final Duration RETENTION_TIME_INSTANCE_METADATA_TOPIC = Duration.ZERO;

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
                                .stepBuilder()
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
                                .retentionTime(RETENTION_TIME_METADATA_TOPIC)
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
                                .stepBuilder()
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
                                .retentionTime(RETENTION_TIME_INSTANCE_METADATA_TOPIC)
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
