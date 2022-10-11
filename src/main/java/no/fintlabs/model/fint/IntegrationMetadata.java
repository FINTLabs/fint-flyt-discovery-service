package no.fintlabs.model.fint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter(AccessLevel.NONE)
    private long id;

    private Long sourceApplicationId;

    private String sourceApplicationIntegrationId;

    private String sourceApplicationIntegrationUri;

    private String integrationDisplayName;

    private Long version;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_metadata_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<InstanceElementMetadata> instanceElementMetadata;

}
