package com.minegraph.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@RelationshipProperties
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuerraClanRel {

    @RelationshipId
    private Long id;

    @TargetNode
    private Clan clan;

    @Property("fechaInicio")
    private LocalDateTime fechaInicio;

    @Property("motivo")
    private String motivo;

    @Property("bajas")
    private Integer bajas;

    @Property("activa")
    private Boolean activa;
}
