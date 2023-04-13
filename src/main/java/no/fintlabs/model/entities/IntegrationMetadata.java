package no.fintlabs.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;

@Getter
@EqualsAndHashCode
@Jacksonized
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

    @Column(nullable = false)
    private Long sourceApplicationId;

    @Column(nullable = false)
    private String sourceApplicationIntegrationId;

    private String sourceApplicationIntegrationUri;

    @Column(nullable = false)
    private String integrationDisplayName;

    @Column(nullable = false)
    private Long version;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private InstanceMetadataContent instanceMetadata;

}
