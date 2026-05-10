package com.minegraph.mapper;

import com.minegraph.dto.response.JugadorResponse;
import com.minegraph.entity.Jugador;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JugadorMapper {

    public JugadorResponse toResponse(Jugador j) {
        if (j == null) return null;
        double kd = (j.getMuertes() != null && j.getMuertes() > 0)
                ? (double) j.getKills() / j.getMuertes() : j.getKills() != null ? j.getKills() : 0.0;

        return JugadorResponse.builder()
                .id(j.getId())
                .nickname(j.getNickname())
                .nivel(j.getNivel())
                .experiencia(j.getExperiencia())
                .fechaRegistro(j.getFechaRegistro())
                .estadoOnline(j.getEstadoOnline())
                .monedas(j.getMonedas())
                .reputacion(j.getReputacion())
                .kills(j.getKills())
                .muertes(j.getMuertes())
                .horasJugadas(j.getHorasJugadas())
                .titulo(j.getTitulo())
                .clanNombre(j.getClan() != null ? j.getClan().getNombre() : null)
                .totalAmigos(j.getAmigos() != null ? j.getAmigos().size() : 0)
                .totalEnemigos(j.getEnemigos() != null ? j.getEnemigos().size() : 0)
                .kdRatio(Math.round(kd * 100.0) / 100.0)
                .build();
    }

    public List<JugadorResponse> toResponseList(List<Jugador> jugadores) {
        return jugadores.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
