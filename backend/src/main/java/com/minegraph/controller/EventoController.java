package com.minegraph.controller;

import com.minegraph.dto.response.EventoResponse;
import com.minegraph.service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> findAll() {
        return ResponseEntity.ok(eventoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> findById(@PathVariable Long id) {
        return eventoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EventoResponse>> findActivos() {
        return ResponseEntity.ok(eventoService.findActivos());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<EventoResponse>> findByTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(eventoService.findByTipo(tipo));
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<EventoResponse>> recientes(@RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(eventoService.findRecientes(limit));
    }

    @GetMapping("/frecuentes")
    public ResponseEntity<List<Map<String, Object>>> frecuentes() {
        return ResponseEntity.ok(eventoService.findFrecuentes());
    }
}
