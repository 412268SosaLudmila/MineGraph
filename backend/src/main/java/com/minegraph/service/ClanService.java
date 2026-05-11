package com.minegraph.service;

import com.minegraph.dto.request.ClanRequest;
import com.minegraph.dto.response.ClanResponse;
import com.minegraph.entity.Clan;
import com.minegraph.mapper.ClanMapper;
import com.minegraph.repository.ClanRepository;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClanService {

    private final ClanRepository clanRepository;
    private final ClanMapper mapper;
    private final Neo4jClient neo4jClient;

    public ClanService(ClanRepository clanRepository, ClanMapper mapper, Neo4jClient neo4jClient) {
        this.clanRepository = clanRepository;
        this.mapper = mapper;
        this.neo4jClient = neo4jClient;
    }

    public List<ClanResponse> findAll() {
        return mapper.toResponseList(clanRepository.findAllByOrderByRankingAsc());
    }

    public Optional<ClanResponse> findById(Long id) {
        return clanRepository.findById(id).map(mapper::toResponse);
    }

    public List<ClanResponse> findDominantes(int limit) {
        return mapper.toResponseList(clanRepository.findClanesDominantes(limit));
    }

    public List<ClanResponse> findEnGuerra() {
        return mapper.toResponseList(clanRepository.findClanesEnGuerra());
    }

    public List<ClanResponse> findTopPorTerritorio(int limit) {
        return mapper.toResponseList(clanRepository.findTopPorTerritorio(limit));
    }

    public List<ClanResponse> findNucleoComunidad() {
        return mapper.toResponseList(clanRepository.findClanesNucleoComunidad());
    }

    public Long countTotal() {
        return clanRepository.countTotal();
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    @Transactional
    public ClanResponse create(ClanRequest req) {
        long count = clanRepository.countTotal();
        Clan c = Clan.builder()
                .nombre(req.getNombre())
                .tag(req.getTag() != null ? req.getTag() : req.getNombre().substring(0, Math.min(4, req.getNombre().length())).toUpperCase())
                .descripcion(req.getDescripcion())
                .nivel(req.getNivel() != null ? req.getNivel() : 1)
                .riqueza(req.getRiqueza() != null ? req.getRiqueza() : 0.0)
                .cantidadMiembros(req.getCantidadMiembros() != null ? req.getCantidadMiembros() : 0)
                .victorias(req.getVictorias() != null ? req.getVictorias() : 0)
                .derrotas(req.getDerrotas() != null ? req.getDerrotas() : 0)
                .territoriosControlados(req.getTerritoriosControlados() != null ? req.getTerritoriosControlados() : 0)
                .color(req.getColor() != null ? req.getColor() : "#6c63ff")
                .ranking((int) count + 1)
                .fechaCreacion(LocalDateTime.now())
                .build();
        return mapper.toResponse(clanRepository.save(c));
    }

    @Transactional
    public Optional<ClanResponse> update(Long id, ClanRequest req) {
        neo4jClient.query(
            "MATCH (c:Clan) WHERE id(c) = $id " +
            "SET c.nombre                = COALESCE($nombre, c.nombre), " +
            "    c.tag                   = COALESCE($tag, c.tag), " +
            "    c.descripcion           = COALESCE($descripcion, c.descripcion), " +
            "    c.nivel                 = COALESCE($nivel, c.nivel), " +
            "    c.riqueza               = COALESCE($riqueza, c.riqueza), " +
            "    c.cantidadMiembros      = COALESCE($cantidadMiembros, c.cantidadMiembros), " +
            "    c.victorias             = COALESCE($victorias, c.victorias), " +
            "    c.derrotas              = COALESCE($derrotas, c.derrotas), " +
            "    c.territoriosControlados= COALESCE($territorios, c.territoriosControlados), " +
            "    c.color                 = COALESCE($color, c.color)")
            .bind(id).to("id")
            .bind(req.getNombre()).to("nombre")
            .bind(req.getTag()).to("tag")
            .bind(req.getDescripcion()).to("descripcion")
            .bind(req.getNivel()).to("nivel")
            .bind(req.getRiqueza()).to("riqueza")
            .bind(req.getCantidadMiembros()).to("cantidadMiembros")
            .bind(req.getVictorias()).to("victorias")
            .bind(req.getDerrotas()).to("derrotas")
            .bind(req.getTerritoriosControlados()).to("territorios")
            .bind(req.getColor()).to("color")
            .run();
        return clanRepository.findById(id).map(mapper::toResponse);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!clanRepository.existsById(id)) return false;
        // Quitar relaciones PERTENECE_A antes de borrar el clan
        neo4jClient.query("MATCH (j:Jugador)-[r:PERTENECE_A]->(c:Clan) WHERE id(c) = $id DELETE r")
                .bind(id).to("id").run();
        neo4jClient.query("MATCH (c:Clan) WHERE id(c) = $id DETACH DELETE c")
                .bind(id).to("id").run();
        return true;
    }

    // ─── Relaciones entre clanes ─────────────────────────────────────────────

    @Transactional
    public void declararAlianza(Long id1, Long id2) {
        neo4jClient.query(
            "MATCH (c1:Clan) WHERE id(c1) = $id1 " +
            "MATCH (c2:Clan) WHERE id(c2) = $id2 " +
            "MERGE (c1)-[:ALIADO_DE]->(c2) MERGE (c2)-[:ALIADO_DE]->(c1)")
            .bind(id1).to("id1").bind(id2).to("id2").run();
    }

    @Transactional
    public void declararGuerra(Long id1, Long id2) {
        neo4jClient.query(
            "MATCH (c1:Clan) WHERE id(c1) = $id1 " +
            "MATCH (c2:Clan) WHERE id(c2) = $id2 " +
            "MERGE (c1)-[g:EN_GUERRA_CON]->(c2) " +
            "SET g.activa = true, g.fechaInicio = toString(datetime()), g.motivo = 'Conflicto territorial', g.bajas = 0")
            .bind(id1).to("id1").bind(id2).to("id2").run();
    }
}
