package com.minegraph.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JugadorRequest {
    private String nickname;
    private Integer nivel;
    private Long experiencia;
    private Boolean estadoOnline;
    private Double monedas;
    private Integer reputacion;
    private Integer kills;
    private Integer muertes;
    private Double horasJugadas;
    private String titulo;
}
