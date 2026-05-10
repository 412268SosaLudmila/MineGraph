package com.minegraph.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@Node("Evento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evento {

    @Id @GeneratedValue
    private Long id;

    @Property("nombre")
    private String nombre;

    @Property("tipo")
    private String tipo;

    @Property("descripcion")
    private String descripcion;

    @Property("fecha")
    private LocalDateTime fecha;

    @Property("duracionMinutos")
    private Integer duracionMinutos;

    @Property("participantes")
    private Integer participantes;

    @Property("recompensa")
    private Double recompensa;

    @Property("region")
    private String region;

    @Property("ganador")
    private String ganador;

    @Property("activo")
    private Boolean activo;
}
