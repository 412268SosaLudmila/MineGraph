package com.minegraph.service;

import com.minegraph.dto.request.JugadorRequest;
import com.minegraph.dto.response.GrafoData;
import com.minegraph.dto.response.JugadorResponse;
import com.minegraph.entity.Jugador;
import com.minegraph.mapper.JugadorMapper;
import com.minegraph.repository.JugadorRepository;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    // ─── CRUD ────────────────────────────────────────────────────────────────

    @Transactional
    public JugadorResponse create(JugadorRequest req) {
        Jugador j = Jugador.builder()
                .nickname(req.getNickname())
                .nivel(req.getNivel() != null ? req.getNivel() : 1)
                .experiencia(req.getExperiencia() != null ? req.getExperiencia() : 0L)
                .estadoOnline(req.getEstadoOnline() != null ? req.getEstadoOnline() : false)
                .monedas(req.getMonedas() != null ? req.getMonedas() : 0.0)
                .reputacion(req.getReputacion() != null ? req.getReputacion() : 0)
                .kills(req.getKills() != null ? req.getKills() : 0)
                .muertes(req.getMuertes() != null ? req.getMuertes() : 0)
                .horasJugadas(req.getHorasJugadas() != null ? req.getHorasJugadas() : 0.0)
                .titulo(req.getTitulo() != null ? req.getTitulo() : "Novato")
                .fechaRegistro(LocalDateTime.now())
                .build();
        return mapper.toResponse(jugadorRepository.save(j));
    }

    @Transactional
    public Optional<JugadorResponse> update(Long id, JugadorRequest req) {
        neo4jClient.query(
            "MATCH (j:Jugador) WHERE id(j) = $id " +
            "SET j.nickname      = COALESCE($nickname, j.nickname), " +
            "    j.nivel         = COALESCE($nivel, j.nivel), " +
            "    j.experiencia   = COALESCE($experiencia, j.experiencia), " +
            "    j.estadoOnline  = COALESCE($estadoOnline, j.estadoOnline), " +
            "    j.monedas       = COALESCE($monedas, j.monedas), " +
            "    j.reputacion    = COALESCE($reputacion, j.reputacion), " +
            "    j.kills         = COALESCE($kills, j.kills), " +
            "    j.muertes       = COALESCE($muertes, j.muertes), " +
            "    j.horasJugadas  = COALESCE($horasJugadas, j.horasJugadas), " +
            "    j.titulo        = COALESCE($titulo, j.titulo)")
            .bind(id).to("id")
            .bind(req.getNickname()).to("nickname")
            .bind(req.getNivel()).to("nivel")
            .bind(req.getExperiencia()).to("experiencia")
            .bind(req.getEstadoOnline()).to("estadoOnline")
            .bind(req.getMonedas()).to("monedas")
            .bind(req.getReputacion()).to("reputacion")
            .bind(req.getKills()).to("kills")
            .bind(req.getMuertes()).to("muertes")
            .bind(req.getHorasJugadas()).to("horasJugadas")
            .bind(req.getTitulo()).to("titulo")
            .run();
        return jugadorRepository.findById(id).map(mapper::toResponse);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!jugadorRepository.existsById(id)) return false;
        neo4jClient.query("MATCH (j:Jugador) WHERE id(j) = $id DETACH DELETE j")
                .bind(id).to("id").run();
        return true;
    }

    // ─── Relaciones ──────────────────────────────────────────────────────────

    @Transactional
    public void addAmigo(Long id, Long amigoId) {
        neo4jClient.query(
            "MATCH (j1:Jugador) WHERE id(j1) = $id " +
            "MATCH (j2:Jugador) WHERE id(j2) = $amigoId " +
            "MERGE (j1)-[:AMIGO_DE]->(j2)")
            .bind(id).to("id").bind(amigoId).to("amigoId").run();
    }

    @Transactional
    public void removeAmigo(Long id, Long amigoId) {
        neo4jClient.query(
            "MATCH (j1:Jugador)-[r:AMIGO_DE]->(j2:Jugador) " +
            "WHERE id(j1) = $id AND id(j2) = $amigoId DELETE r")
            .bind(id).to("id").bind(amigoId).to("amigoId").run();
    }

    @Transactional
    public void addEnemigo(Long id, Long enemigoId) {
        neo4jClient.query(
            "MATCH (j1:Jugador) WHERE id(j1) = $id " +
            "MATCH (j2:Jugador) WHERE id(j2) = $enemigoId " +
            "MERGE (j1)-[:ENEMIGO_DE]->(j2)")
            .bind(id).to("id").bind(enemigoId).to("enemigoId").run();
    }

    @Transactional
    public void removeEnemigo(Long id, Long enemigoId) {
        neo4jClient.query(
            "MATCH (j1:Jugador)-[r:ENEMIGO_DE]->(j2:Jugador) " +
            "WHERE id(j1) = $id AND id(j2) = $enemigoId DELETE r")
            .bind(id).to("id").bind(enemigoId).to("enemigoId").run();
    }

    @Transactional
    public void asignarClan(Long id, Long clanId) {
        neo4jClient.query(
            "MATCH (j:Jugador) WHERE id(j) = $id " +
            "OPTIONAL MATCH (j)-[old:PERTENECE_A]->(:Clan) DELETE old " +
            "WITH j MATCH (c:Clan) WHERE id(c) = $clanId " +
            "MERGE (j)-[:PERTENECE_A]->(c)")
            .bind(id).to("id").bind(clanId).to("clanId").run();
    }

    @Transactional
    public void quitarClan(Long id) {
        neo4jClient.query(
            "MATCH (j:Jugador)-[r:PERTENECE_A]->(:Clan) WHERE id(j) = $id DELETE r")
            .bind(id).to("id").run();
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
