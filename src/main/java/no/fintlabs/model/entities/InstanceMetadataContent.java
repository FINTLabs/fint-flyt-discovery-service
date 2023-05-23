package no.fintlabs.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Getter
@Setter
@EqualsAndHashCode
@Jacksonized
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<@Valid @NotNull InstanceValueMetadata> instanceValueMetadata;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<@Valid @NotNull InstanceObjectCollectionMetadata> instanceObjectCollectionMetadata;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<@Valid @NotNull InstanceMetadataCategory> categories;

}
