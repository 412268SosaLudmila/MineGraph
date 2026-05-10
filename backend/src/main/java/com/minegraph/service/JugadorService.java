package com.minegraph.service;

import com.minegraph.dto.response.GrafoData;
import com.minegraph.dto.response.JugadorResponse;
import com.minegraph.entity.Jugador;
import com.minegraph.mapper.JugadorMapper;
import com.minegraph.repository.JugadorRepository;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final JugadorMapper mapper;
    private final Neo4jClient neo4jClient;

    public JugadorService(JugadorRepository jugadorRepository, JugadorMapper mapper, Neo4jClient neo4jClient) {
        this.jugadorRepository = jugadorRepository;
        this.mapper = mapper;
        this.neo4jClient = neo4jClient;
    }

    public List<JugadorResponse> findAll() {
        return mapper.toResponseList(jugadorRepository.findAll());
    }

    public Optional<JugadorResponse> findById(Long id) {
        return jugadorRepository.findById(id).map(mapper::toResponse);
    }

    public Optional<JugadorResponse> findByNickname(String nickname) {
        return jugadorRepository.findByNickname(nickname).map(mapper::toResponse);
    }

    public List<JugadorResponse> findOnline() {
        return mapper.toResponseList(jugadorRepository.findByEstadoOnlineTrue());
    }

    public List<JugadorResponse> findMasConectados(int limit) {
        return mapper.toResponseList(jugadorRepository.findMasConectados(limit));
    }

    public List<JugadorResponse> findTopPvP(int limit) {
        return mapper.toResponseList(jugadorRepository.findTopPvP(limit));
    }

    public List<JugadorResponse> findTopComerciantes(int limit) {
        return mapper.toResponseList(jugadorRepository.findTopComerciantes(limit));
    }

    public List<JugadorResponse> findMasInfluyentes(int limit) {
        return mapper.toResponseList(jugadorRepository.findMasInfluyentes(limit));
    }

    public List<JugadorResponse> findAmigosDeAmigos(String nickname) {
        return mapper.toResponseList(jugadorRepository.findAmigosDeAmigos(nickname));
    }

    public List<JugadorResponse> search(String query) {
        String pattern = "(?i).*" + query + ".*";
        return mapper.toResponseList(jugadorRepository.searchByNickname(pattern));
    }

    public GrafoData buildGrafoJugadores() {
        List<Jugador> jugadores = jugadorRepository.findAll();
        List<GrafoData.GrafoNodo> nodes = new ArrayList<>();
        List<GrafoData.GrafoArista> edges = new ArrayList<>();
        Set<String> edgeIds = new HashSet<>();

        for (Jugador j : jugadores) {
            int size = 15 + (j.getNivel() != null ? Math.min(j.getNivel() / 5, 20) : 0);
            nodes.add(GrafoData.GrafoNodo.builder()
                    .id(j.getId().toString())
                    .label(j.getNickname())
                    .group(j.getClan() != null ? j.getClan().getNombre() : "Sin Clan")
                    .nivel(j.getNivel())
                    .online(j.getEstadoOnline())
                    .clan(j.getClan() != null ? j.getClan().getNombre() : null)
                    .size(size)
                    .titulo(j.getTitulo())
                    .build());
        }

        // Neo4jClient bypasses SDN entity mapping to get raw relationship data
        neo4jClient.query(
            "MATCH (j1:Jugador)-[:AMIGO_DE]->(j2:Jugador) " +
            "RETURN id(j1) AS fromId, id(j2) AS toId LIMIT 500")
            .fetch().all().forEach(row -> {
                long fromId = toLong(row.get("fromId"));
                long toId   = toLong(row.get("toId"));
                String edgeId = "a_" + Math.min(fromId, toId) + "_" + Math.max(fromId, toId);
                if (edgeIds.add(edgeId)) {
                    edges.add(GrafoData.GrafoArista.builder()
                            .id(edgeId).from(String.valueOf(fromId)).to(String.valueOf(toId))
                            .tipo("AMIGO_DE").color("#00ff88").width(1.5).build());
                }
            });

        neo4jClient.query(
            "MATCH (j1:Jugador)-[:ENEMIGO_DE]->(j2:Jugador) " +
            "RETURN id(j1) AS fromId, id(j2) AS toId LIMIT 300")
            .fetch().all().forEach(row -> {
                long fromId = toLong(row.get("fromId"));
                long toId   = toLong(row.get("toId"));
                String edgeId = "e_" + fromId + "_" + toId;
                if (edgeIds.add(edgeId)) {
                    edges.add(GrafoData.GrafoArista.builder()
                            .id(edgeId).from(String.valueOf(fromId)).to(String.valueOf(toId))
                            .tipo("ENEMIGO_DE").color("#ff4444").width(2.0).build());
                }
            });

        neo4jClient.query(
            "MATCH (j1:Jugador)-[r:COMERCIA_CON]->(j2:Jugador) " +
            "RETURN id(j1) AS fromId, id(j2) AS toId, r.volumenTotal AS vol LIMIT 400")
            .fetch().all().forEach(row -> {
                long fromId = toLong(row.get("fromId"));
                long toId   = toLong(row.get("toId"));
                String edgeId = "c_" + fromId + "_" + toId;
                if (edgeIds.add(edgeId)) {
                    double vol = row.get("vol") != null ? ((Number) row.get("vol")).doubleValue() : 1000.0;
                    edges.add(GrafoData.GrafoArista.builder()
                            .id(edgeId).from(String.valueOf(fromId)).to(String.valueOf(toId))
                            .tipo("COMERCIA_CON").color("#ffd700")
                            .width(Math.min(vol / 5000.0, 5.0)).build());
                }
            });

        return GrafoData.builder().nodes(nodes).edges(edges).build();
    }

    private static long toLong(Object value) {
        return value instanceof Number n ? n.longValue() : Long.parseLong(value.toString());
    }

    public Long countOnline() {
        return jugadorRepository.countOnline();
    }

    public Long countTotal() {
        return jugadorRepository.countTotal();
    }
}
