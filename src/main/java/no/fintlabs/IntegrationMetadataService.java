package no.fintlabs;

import no.fintlabs.mapping.IntegrationMetadataMappingService;
import no.fintlabs.model.dtos.InstanceMetadataContentDto;
import no.fintlabs.model.dtos.IntegrationMetadataDto;
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
            long sourceApplicationId, String sourceApplicationIntegrationId
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

    public boolean versionExists(IntegrationMetadataDto integrationMetadataDto) {
        return integrationMetadataRepository.existsBySourceApplicationIdAndAndSourceApplicationIntegrationIdAndVersion(
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
