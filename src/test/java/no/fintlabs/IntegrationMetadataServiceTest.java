package no.fintlabs;

import no.fintlabs.mapping.IntegrationMetadataMappingService;
import no.fintlabs.model.dtos.InstanceMetadataContentDto;
import no.fintlabs.model.dtos.IntegrationMetadataDto;
import no.fintlabs.model.entities.IntegrationMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntegrationMetadataServiceTest {

    @Mock
    private IntegrationMetadataRepository integrationMetadataRepository;

    @Mock
    private IntegrationMetadataMappingService integrationMetadataMappingService;

    @InjectMocks
    private IntegrationMetadataService service;

    private final Long sourceApplicationId = 1L;

    @Mock
    private IntegrationMetadataDto dto;

    @Mock
    private IntegrationMetadata entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetIntegrationMetadataForSourceApplicationWithLatestVersions() {
        when(integrationMetadataRepository.findAllWithLatestVersionsForSourceApplication(sourceApplicationId))
                .thenReturn(List.of(entity));
        when(integrationMetadataMappingService.toDto(entity)).thenReturn(dto);

        List<IntegrationMetadataDto> result = (List<IntegrationMetadataDto>) service.getIntegrationMetadataForSourceApplication(sourceApplicationId, true);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetIntegrationMetadataForSourceApplicationWithoutLatestVersions() {
        when(integrationMetadataRepository.findAllBySourceApplicationId(sourceApplicationId))
                .thenReturn(List.of(entity));
        when(integrationMetadataMappingService.toDto(entity))
                .thenReturn(dto);

        List<IntegrationMetadataDto> result = (List<IntegrationMetadataDto>) service.getIntegrationMetadataForSourceApplication(sourceApplicationId, false);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetAllForSourceApplicationIdAndSourceApplicationIntegrationId() {
        String integrationId = "integrationId";

        when(integrationMetadataRepository.findAllBySourceApplicationIdAndSourceApplicationIntegrationId(sourceApplicationId, integrationId))
                .thenReturn(List.of(entity));
        when(integrationMetadataMappingService.toDto(entity))
                .thenReturn(dto);

        List<IntegrationMetadataDto> result = (List<IntegrationMetadataDto>) service.getAllForSourceApplicationIdAndSourceApplicationIntegrationId(sourceApplicationId, integrationId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetInstanceMetadataById() {
        long id = 1L;
        InstanceMetadataContentDto instanceDto = mock(InstanceMetadataContentDto.class);

        when(integrationMetadataRepository.findById(id)).thenReturn(Optional.of(entity));
        when(integrationMetadataMappingService.toDto(entity)).thenReturn(dto);
        when(dto.getInstanceMetadata()).thenReturn(instanceDto);

        Optional<InstanceMetadataContentDto> result = service.getInstanceMetadataById(id);

        assertTrue(result.isPresent());
        assertEquals(instanceDto, result.get());
    }

    @Test
    void testVersionExists() {
        when(dto.getSourceApplicationId()).thenReturn(sourceApplicationId);
        when(dto.getSourceApplicationIntegrationId()).thenReturn("integrationId");
        when(dto.getVersion()).thenReturn(1L);
        when(integrationMetadataRepository.existsBySourceApplicationIdAndAndSourceApplicationIntegrationIdAndVersion(
                dto.getSourceApplicationId(), dto.getSourceApplicationIntegrationId(), dto.getVersion())).thenReturn(true);

        boolean result = service.versionExists(dto);

        assertTrue(result);
    }

    @Test
    void testSave() {
        IntegrationMetadataDto savedDto = mock(IntegrationMetadataDto.class);

        when(integrationMetadataMappingService.toEntity(dto)).thenReturn(entity);
        when(integrationMetadataRepository.save(entity)).thenReturn(entity);
        when(integrationMetadataMappingService.toDto(entity)).thenReturn(savedDto);

        IntegrationMetadataDto result = service.save(dto);

        assertEquals(savedDto, result);
    }
}
