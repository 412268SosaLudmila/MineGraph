package com.minegraph.service;

import com.minegraph.dto.response.DashboardStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final JugadorService jugadorService;
    private final ClanService clanService;
    private final EventoService eventoService;
    private final EconomiaService economiaService;

    public DashboardService(JugadorService jugadorService,
                             ClanService clanService,
                             EventoService eventoService,
                             EconomiaService economiaService) {
        this.jugadorService = jugadorService;
        this.clanService = clanService;
        this.eventoService = eventoService;
        this.economiaService = economiaService;
    }

    public DashboardStats getFullStats() {
        return DashboardStats.builder()
                .totalJugadores(jugadorService.countTotal())
                .jugadoresOnline(jugadorService.countOnline())
                .totalClanes(clanService.countTotal())
                .totalEventos(eventoService.countTotal())
                .totalTransacciones(economiaService.countTotal())
                .volumenEconomico(economiaService.getVolumenTotal())
                .topPvP(jugadorService.findTopPvP(10))
                .jugadoresMasConectados(jugadorService.findMasConectados(10))
                .clanesDominantes(clanService.findDominantes(10))
                .topComerciantes(jugadorService.findTopComerciantes(10))
                .eventosRecientes(eventoService.findRecientes(10))
                .topItems(economiaService.getStats().getTopItems())
                .volumenPorMercado(economiaService.getStats().getVolumenPorMercado())
                .jugadoresNuevos(jugadorService.findAll().stream().limit(10).toList())
                .eventosFrecuentes(eventoService.findFrecuentes())
                .actividadPorHora(generarActividadSimulada())
                .distribucionNiveles(generarDistribucionNiveles())
                .build();
    }

    private List<Map<String, Object>> generarActividadSimulada() {
        List<Map<String, Object>> actividad = new ArrayList<>();
        String[] horas = {"00:00","02:00","04:00","06:00","08:00","10:00","12:00",
                          "14:00","16:00","18:00","20:00","22:00"};
        int[] valores = {12,8,5,18,45,78,92,88,95,110,125,98};
        for (int i = 0; i < horas.length; i++) {
            actividad.add(Map.of("hora", horas[i], "jugadores", valores[i]));
        }
        return actividad;
    }

    private List<Map<String, Object>> generarDistribucionNiveles() {
        List<Map<String, Object>> dist = new ArrayList<>();
        dist.add(Map.of("rango", "1-10", "cantidad", 15));
        dist.add(Map.of("rango", "11-25", "cantidad", 28));
        dist.add(Map.of("rango", "26-50", "cantidad", 32));
        dist.add(Map.of("rango", "51-75", "cantidad", 18));
        dist.add(Map.of("rango", "76-100", "cantidad", 7));
        return dist;
    }
}
