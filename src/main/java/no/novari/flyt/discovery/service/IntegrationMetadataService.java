package no.novari.flyt.discovery.service;

import no.novari.flyt.discovery.service.mapping.IntegrationMetadataMappingService;
import no.novari.flyt.discovery.service.model.dtos.InstanceMetadataContentDto;
import no.novari.flyt.discovery.service.model.dtos.IntegrationMetadataDto;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class IntegrationMetadataService {

    private final IntegrationMetadataRepository integrationMetadataRepository;
    private final IntegrationMetadataMappingService integrationMetadataMappingService;

    public IntegrationMetadataService(
            IntegrationMetadataRepository integrationMetadataRepository,
            IntegrationMetadataMappingService integrationMetadataMappingService
    ) {
        this.integrationMetadataRepository = integrationMetadataRepository;
        this.integrationMetadataMappingService = integrationMetadataMappingService;
    }

    public Collection<IntegrationMetadataDto> getIntegrationMetadataForSourceApplication(Long sourceApplicationId, boolean onlyLatestVersions) {
        return (onlyLatestVersions
                ? integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(sourceApplicationId)
                : integrationMetadataRepository.findAllBySourceApplicationId(sourceApplicationId))
                .stream()
                .map(integrationMetadataMappingService::toDto)
                .toList();
    }

    public Collection<IntegrationMetadataDto> getAllForSourceApplicationIdAndSourceApplicationIntegrationId(
            long sourceApplicationId,
            String sourceApplicationIntegrationId
    ) {
        return integrationMetadataRepository
                .findAllBySourceApplicationIdAndSourceApplicationIntegrationId(
                        sourceApplicationId,
                        sourceApplicationIntegrationId
                )
                .stream()
                .map(integrationMetadataMappingService::toDto)
                .toList();
    }

    public Optional<InstanceMetadataContentDto> getInstanceMetadataById(long id) {
        return integrationMetadataRepository
                .findById(id)
                .map(integrationMetadataMappingService::toDto)
                .map(IntegrationMetadataDto::getInstanceMetadata);
    }

    public Optional<IntegrationMetadataDto> getById(long id) {
        return integrationMetadataRepository
                .findById(id)
                .map(integrationMetadataMappingService::toDto);
    }

    public boolean versionExists(IntegrationMetadataDto integrationMetadataDto) {
        return integrationMetadataRepository.existsBySourceApplicationIdAndSourceApplicationIntegrationIdAndVersion(
                integrationMetadataDto.getSourceApplicationId(),
                integrationMetadataDto.getSourceApplicationIntegrationId(),
                integrationMetadataDto.getVersion()
        );
    }

    public IntegrationMetadataDto save(IntegrationMetadataDto integrationMetadataDto) {
        return integrationMetadataMappingService.toDto(
                integrationMetadataRepository.save(
                        integrationMetadataMappingService.toEntity(integrationMetadataDto)
                )
        );
    }

}
