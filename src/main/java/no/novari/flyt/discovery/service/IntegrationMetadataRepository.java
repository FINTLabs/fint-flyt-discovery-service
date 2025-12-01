package no.novari.flyt.discovery.service;

import no.novari.flyt.discovery.service.model.entities.IntegrationMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface IntegrationMetadataRepository extends JpaRepository<IntegrationMetadata, Long> {

    @Query(value = "SELECT im" +
            " FROM IntegrationMetadata im" +
            " WHERE im.sourceApplicationId = :sourceApplicationId" +
            " AND im.version IN (" +
            "   SELECT MAX(iim.version)" +
            "   FROM IntegrationMetadata iim" +
            "   WHERE im.sourceApplicationId = iim.sourceApplicationId" +
            "   AND im.sourceApplicationIntegrationId = iim.sourceApplicationIntegrationId" +
            " )"
    )
    Collection<IntegrationMetadata> findAllWithLatestVersionsForSourceApplication(
            @Param(value = "sourceApplicationId") Long sourceApplicationId
    );

    Collection<IntegrationMetadata> findAllBySourceApplicationId(
            Long sourceApplicationId
    );

    Collection<IntegrationMetadata> findAllBySourceApplicationIdAndSourceApplicationIntegrationId(
            Long sourceApplicationId,
            String sourceApplicationIntegrationId
    );

    boolean existsBySourceApplicationIdAndSourceApplicationIntegrationIdAndVersion(
            Long sourceApplicationId,
            String sourceApplicationIntegrationId,
            Long version
    );

}
