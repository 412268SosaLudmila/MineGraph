package com.minegraph.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClanResponse {
    private Long id;
    private String nombre;
    private String tag;
    private Integer ranking;
    private Double riqueza;
    private Integer cantidadMiembros;
    private LocalDateTime fechaCreacion;
    private String descripcion;
    private Integer nivel;
    private Integer victorias;
    private Integer derrotas;
    private Integer territoriosControlados;
    private String color;
    private Integer totalAliados;
    private Integer guerrasActivas;
}
