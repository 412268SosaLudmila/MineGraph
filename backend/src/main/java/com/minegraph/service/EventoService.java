package com.minegraph.service;

import com.minegraph.dto.request.EventoRequest;
import com.minegraph.dto.response.EventoResponse;
import com.minegraph.entity.Evento;
import com.minegraph.mapper.EventoMapper;
import com.minegraph.repository.EventoRepository;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper mapper;
    private final Neo4jClient neo4jClient;

    public EventoService(EventoRepository eventoRepository, EventoMapper mapper, Neo4jClient neo4jClient) {
        this.eventoRepository = eventoRepository;
        this.mapper = mapper;
        this.neo4jClient = neo4jClient;
    }

    public List<EventoResponse> findAll() {
        return mapper.toResponseList(eventoRepository.findAllByOrderByFechaDesc());
    }

    public Optional<EventoResponse> findById(Long id) {
        return eventoRepository.findById(id).map(mapper::toResponse);
    }

    public List<EventoResponse> findActivos() {
        return mapper.toResponseList(eventoRepository.findByActivoTrue());
    }

    public List<EventoResponse> findByTipo(String tipo) {
        return mapper.toResponseList(eventoRepository.findByTipo(tipo));
    }

    public List<EventoResponse> findRecientes(int limit) {
        return mapper.toResponseList(eventoRepository.findEventosRecientes(limit));
    }

    public List<Map<String, Object>> findFrecuentes() {
        List<Evento> todos = eventoRepository.findAll();
        return todos.stream()
                .collect(Collectors.groupingBy(Evento::getTipo, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("tipo", e.getKey());
                    m.put("cantidad", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    public Long countTotal() {
        return eventoRepository.countTotal();
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    @Transactional
    public EventoResponse create(EventoRequest req) {
        Evento e = Evento.builder()
                .nombre(req.getNombre())
                .tipo(req.getTipo() != null ? req.getTipo() : "PvP")
                .descripcion(req.getDescripcion())
                .fecha(LocalDateTime.now())
                .duracionMinutos(req.getDuracionMinutos() != null ? req.getDuracionMinutos() : 60)
                .participantes(req.getParticipantes() != null ? req.getParticipantes() : 0)
                .recompensa(req.getRecompensa() != null ? req.getRecompensa() : 0.0)
                .region(req.getRegion() != null ? req.getRegion() : "Spawn")
                .ganador(req.getGanador())
                .activo(req.getActivo() != null ? req.getActivo() : true)
                .build();
        return mapper.toResponse(eventoRepository.save(e));
    }

    @Transactional
    public Optional<EventoResponse> update(Long id, EventoRequest req) {
        neo4jClient.query(
            "MATCH (e:Evento) WHERE id(e) = $id " +
            "SET e.nombre          = COALESCE($nombre, e.nombre), " +
            "    e.tipo            = COALESCE($tipo, e.tipo), " +
            "    e.descripcion     = COALESCE($descripcion, e.descripcion), " +
            "    e.duracionMinutos = COALESCE($duracion, e.duracionMinutos), " +
            "    e.participantes   = COALESCE($participantes, e.participantes), " +
            "    e.recompensa      = COALESCE($recompensa, e.recompensa), " +
            "    e.region          = COALESCE($region, e.region), " +
            "    e.ganador         = COALESCE($ganador, e.ganador), " +
            "    e.activo          = COALESCE($activo, e.activo)")
            .bind(id).to("id")
            .bind(req.getNombre()).to("nombre")
            .bind(req.getTipo()).to("tipo")
            .bind(req.getDescripcion()).to("descripcion")
            .bind(req.getDuracionMinutos()).to("duracion")
            .bind(req.getParticipantes()).to("participantes")
            .bind(req.getRecompensa()).to("recompensa")
            .bind(req.getRegion()).to("region")
            .bind(req.getGanador()).to("ganador")
            .bind(req.getActivo()).to("activo")
            .run();
        return eventoRepository.findById(id).map(mapper::toResponse);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!eventoRepository.existsById(id)) return false;
        neo4jClient.query("MATCH (e:Evento) WHERE id(e) = $id DETACH DELETE e")
                .bind(id).to("id").run();
        return true;
    }
}
