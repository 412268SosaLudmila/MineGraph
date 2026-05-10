package com.minegraph.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@Node("Transaccion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaccion {

    @Id @GeneratedValue
    private Long id;

    @Property("vendedorNick")
    private String vendedorNick;

    @Property("compradorNick")
    private String compradorNick;

    @Property("itemNombre")
    private String itemNombre;

    @Property("cantidad")
    private Integer cantidad;

    @Property("precioUnitario")
    private Double precioUnitario;

    @Property("total")
    private Double total;

    @Property("fecha")
    private LocalDateTime fecha;

    @Property("mercado")
    private String mercado;

    @Property("tipo")
    private String tipo;
}
