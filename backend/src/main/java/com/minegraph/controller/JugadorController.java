package com.minegraph.controller;

import com.minegraph.dto.response.GrafoData;
import com.minegraph.dto.response.JugadorResponse;
import com.minegraph.service.JugadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @GetMapping
    public ResponseEntity<List<JugadorResponse>> findAll() {
        return ResponseEntity.ok(jugadorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JugadorResponse> findById(@PathVariable Long id) {
        return jugadorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/online")
    public ResponseEntity<List<JugadorResponse>> findOnline() {
        return ResponseEntity.ok(jugadorService.findOnline());
    }

    @GetMapping("/top/pvp")
    public ResponseEntity<List<JugadorResponse>> topPvP(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(jugadorService.findTopPvP(limit));
    }

    @GetMapping("/top/comerciantes")
    public ResponseEntity<List<JugadorResponse>> topComerciantes(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(jugadorService.findTopComerciantes(limit));
    }

    @GetMapping("/top/conectados")
    public ResponseEntity<List<JugadorResponse>> topConectados(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(jugadorService.findMasConectados(limit));
    }

    @GetMapping("/top/influyentes")
    public ResponseEntity<List<JugadorResponse>> topInfluyentes(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(jugadorService.findMasInfluyentes(limit));
    }

    @GetMapping("/{nickname}/amigos-de-amigos")
    public ResponseEntity<List<JugadorResponse>> amigosDeAmigos(@PathVariable String nickname) {
        return ResponseEntity.ok(jugadorService.findAmigosDeAmigos(nickname));
    }

    @GetMapping("/search")
    public ResponseEntity<List<JugadorResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(jugadorService.search(q));
    }

    @GetMapping("/grafo")
    public ResponseEntity<GrafoData> getGrafo() {
        return ResponseEntity.ok(jugadorService.buildGrafoJugadores());
    }
}
