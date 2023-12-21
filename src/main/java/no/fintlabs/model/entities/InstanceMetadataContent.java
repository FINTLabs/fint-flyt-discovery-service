package no.fintlabs.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceMetadataContent that = (InstanceMetadataContent) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
