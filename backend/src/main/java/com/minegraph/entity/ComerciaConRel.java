package com.minegraph.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@RelationshipProperties
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComerciaConRel {

    @RelationshipId
    private Long id;

    @TargetNode
    private Jugador jugador;

    @Property("volumenTotal")
    private Double volumenTotal;

    @Property("cantidadTransacciones")
    private Integer cantidadTransacciones;

    @Property("ultimaFecha")
    private LocalDateTime ultimaFecha;

    @Property("itemMasComerciado")
    private String itemMasComerciado;
}
