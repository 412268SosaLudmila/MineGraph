package com.minegraph.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JugadorResponse {
    private Long id;
    private String nickname;
    private Integer nivel;
    private Long experiencia;
    private LocalDateTime fechaRegistro;
    private Boolean estadoOnline;
    private Double monedas;
    private Integer reputacion;
    private Integer kills;
    private Integer muertes;
    private Double horasJugadas;
    private String titulo;
    private String clanNombre;
    private Integer totalAmigos;
    private Integer totalEnemigos;
    private Double kdRatio;
}
