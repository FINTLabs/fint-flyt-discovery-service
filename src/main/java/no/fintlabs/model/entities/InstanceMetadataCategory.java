package no.fintlabs.model.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;

@Getter
@EqualsAndHashCode
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class InstanceMetadataCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private long id;

    private String displayName;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private InstanceMetadataContent content;

}
