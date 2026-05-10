package com.minegraph.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStats {

    // Contadores generales
    private Long totalJugadores;
    private Long jugadoresOnline;
    private Long totalClanes;
    private Long totalEventos;
    private Long totalTransacciones;
    private Double volumenEconomico;

    // Rankings
    private List<JugadorResponse> topPvP;
    private List<JugadorResponse> jugadoresMasConectados;
    private List<ClanResponse> clanesDominantes;
    private List<JugadorResponse> topComerciantes;

    // Eventos recientes
    private List<EventoResponse> eventosRecientes;

    // Estadísticas económicas
    private List<Map<String, Object>> topItems;
    private List<Map<String, Object>> volumenPorMercado;

    // Actividad
    private List<JugadorResponse> jugadoresNuevos;
    private List<Map<String, Object>> eventosFrecuentes;

    // Gráficos de tendencia (datos por hora/día)
    private List<Map<String, Object>> actividadPorHora;
    private List<Map<String, Object>> distribucionNiveles;
}
