package com.minegraph.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventoResponse {
    private Long id;
    private String nombre;
    private String tipo;
    private String descripcion;
    private LocalDateTime fecha;
    private Integer duracionMinutos;
    private Integer participantes;
    private Double recompensa;
    private String region;
    private String ganador;
    private Boolean activo;
}
