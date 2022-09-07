package no.fintlabs.model.fint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "UniqueSourceApplicationIdAndSourceApplicationIntegrationIdAndVersion",
                columnNames = {"sourceApplicationId", "sourceApplicationIntegrationId", "version"}
        )
})
public class IntegrationMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private long id;

    private String sourceApplicationId;

    private String sourceApplicationIntegrationId;

    private String sourceApplicationIntegrationUri;

    private String integrationDisplayName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "integration_metadata_id")
    private List<InstanceElementMetadata> instanceElementMetadata;

    private Integer version;

}
