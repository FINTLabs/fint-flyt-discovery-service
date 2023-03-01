package no.fintlabs.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class InstanceMetadataContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private long id;

    @OneToMany
    private Collection<@Valid @NotNull InstanceValueMetadata> instanceValueMetadata;

    @OneToMany
    private Collection<@Valid @NotNull InstanceObjectCollectionMetadata> instanceObjectCollectionMetadata;

    @OneToMany
    private Collection<@Valid @NotNull InstanceMetadataCategory> categories;

}
