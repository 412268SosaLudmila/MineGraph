package com.minegraph.service;

import com.minegraph.dto.response.EventoResponse;
import com.minegraph.entity.Evento;
import com.minegraph.mapper.EventoMapper;
import com.minegraph.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper mapper;

    public EventoService(EventoRepository eventoRepository, EventoMapper mapper) {
        this.eventoRepository = eventoRepository;
        this.mapper = mapper;
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
}
