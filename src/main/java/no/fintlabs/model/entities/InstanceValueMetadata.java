package no.fintlabs.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class InstanceValueMetadata {

    public enum Type {
        STRING,
        BOOLEAN,
        FILE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private long id;

    private String displayName;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String key;

}
