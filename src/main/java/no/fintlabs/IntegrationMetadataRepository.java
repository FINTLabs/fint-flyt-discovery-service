package no.fintlabs;

import no.fintlabs.model.fint.IntegrationMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface IntegrationMetadataRepository extends JpaRepository<IntegrationMetadata, Long> {

    default int findNextVersion(
            Long sourceApplicationId,
            String sourceApplicationIntegrationId
    ) {
        return findCurrentVersion(sourceApplicationId, sourceApplicationIntegrationId)
                .map(v -> v + 1)
                .orElse(1);
    }

    @Query(value = "SELECT MAX(im.version)" +
            " FROM IntegrationMetadata im" +
            " WHERE im.sourceApplicationId = :sourceApplicationId" +
            " AND im.sourceApplicationIntegrationId LIKE :sourceApplicationIntegrationId"
    )
    Optional<Integer> findCurrentVersion(
            @Param(value = "sourceApplicationId") Long sourceApplicationId,
            @Param(value = "sourceApplicationIntegrationId") String sourceApplicationIntegrationId
    );

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

    Collection<IntegrationMetadata> findAllBySourceApplicationIdAndSourceApplicationIntegrationId(
            Long sourceApplicationId,
            String sourceApplicationIntegrationId
    );

}
