package com.minegraph.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventoRequest {
    private String nombre;
    private String tipo;
    private String descripcion;
    private Integer duracionMinutos;
    private Integer participantes;
    private Double recompensa;
    private String region;
    private String ganador;
    private Boolean activo;
}
