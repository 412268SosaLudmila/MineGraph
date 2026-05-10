package com.minegraph.mapper;

import com.minegraph.dto.response.ClanResponse;
import com.minegraph.entity.Clan;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClanMapper {

    public ClanResponse toResponse(Clan c) {
        if (c == null) return null;
        long guerrasActivas = c.getGuerras() != null
                ? c.getGuerras().stream().filter(g -> Boolean.TRUE.equals(g.getActiva())).count() : 0;

        return ClanResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .tag(c.getTag())
                .ranking(c.getRanking())
                .riqueza(c.getRiqueza())
                .cantidadMiembros(c.getCantidadMiembros())
                .fechaCreacion(c.getFechaCreacion())
                .descripcion(c.getDescripcion())
                .nivel(c.getNivel())
                .victorias(c.getVictorias())
                .derrotas(c.getDerrotas())
                .territoriosControlados(c.getTerritoriosControlados())
                .color(c.getColor())
                .totalAliados(c.getAliados() != null ? c.getAliados().size() : 0)
                .guerrasActivas((int) guerrasActivas)
                .build();
    }

    public List<ClanResponse> toResponseList(List<Clan> clanes) {
        return clanes.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
