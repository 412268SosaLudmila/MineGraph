package com.minegraph.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

@Node("Item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Item {

    @Id @GeneratedValue
    private Long id;

    @Property("nombre")
    private String nombre;

    @Property("tipo")
    private String tipo;

    @Property("rareza")
    private String rareza;

    @Property("precioBase")
    private Double precioBase;

    @Property("descripcion")
    private String descripcion;

    @Property("stackeable")
    private Boolean stackeable;
}
