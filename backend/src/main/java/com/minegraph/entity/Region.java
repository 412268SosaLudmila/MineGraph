package com.minegraph.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

@Node("Region")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Region {

    @Id @GeneratedValue
    private Long id;

    @Property("nombre")
    private String nombre;

    @Property("tipo")
    private String tipo;

    @Property("peligrosidad")
    private Integer peligrosidad;

    @Property("visitasTotal")
    private Long visitasTotal;

    @Property("coordX")
    private Integer coordX;

    @Property("coordZ")
    private Integer coordZ;

    @Property("activa")
    private Boolean activa;

    @Property("propietario")
    private String propietario;
}
