package com.minegraph.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@Node("Mercado")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mercado {

    @Id @GeneratedValue
    private Long id;

    @Property("nombre")
    private String nombre;

    @Property("region")
    private String region;

    @Property("activo")
    private Boolean activo;

    @Property("volumenDiario")
    private Double volumenDiario;

    @Property("cantidadOfertas")
    private Integer cantidadOfertas;

    @Property("fechaApertura")
    private LocalDateTime fechaApertura;
}
