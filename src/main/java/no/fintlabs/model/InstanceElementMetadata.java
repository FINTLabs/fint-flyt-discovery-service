package no.fintlabs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class InstanceElementMetadata {

    public enum Type {
        STRING,
        DATE,
        DATETIME,
        URL,
        EMAIL,
        PHONE,
        BOOLEAN,
        INTEGER,
        DOUBLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private long id;

    private String key;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String displayName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "parent_instance_element_metadata_id")
    private List<InstanceElementMetadata> children;

}
