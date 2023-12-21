package no.fintlabs.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class InstanceObjectCollectionMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private long id;

    private String displayName;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private InstanceMetadataContent objectMetadata;

    @Column(name = "\"key\"")
    private String key;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceObjectCollectionMetadata that = (InstanceObjectCollectionMetadata) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
