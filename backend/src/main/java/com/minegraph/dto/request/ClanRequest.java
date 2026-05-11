package com.minegraph.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClanRequest {
    private String nombre;
    private String tag;
    private String descripcion;
    private Integer nivel;
    private Double riqueza;
    private Integer cantidadMiembros;
    private Integer victorias;
    private Integer derrotas;
    private Integer territoriosControlados;
    private String color;
}
