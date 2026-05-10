package com.minegraph.mapper;

import com.minegraph.dto.response.EventoResponse;
import com.minegraph.entity.Evento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventoMapper {

    public EventoResponse toResponse(Evento e) {
        if (e == null) return null;
        return EventoResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .tipo(e.getTipo())
                .descripcion(e.getDescripcion())
                .fecha(e.getFecha())
                .duracionMinutos(e.getDuracionMinutos())
                .participantes(e.getParticipantes())
                .recompensa(e.getRecompensa())
                .region(e.getRegion())
                .ganador(e.getGanador())
                .activo(e.getActivo())
                .build();
    }

    public List<EventoResponse> toResponseList(List<Evento> eventos) {
        return eventos.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
