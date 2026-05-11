package com.minegraph.controller;

import com.minegraph.dto.request.JugadorRequest;
import com.minegraph.dto.response.GrafoData;
import com.minegraph.dto.response.JugadorResponse;
import com.minegraph.service.JugadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    // ─── GET ─────────────────────────────────────────────────────────────────

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

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<JugadorResponse> create(@RequestBody JugadorRequest req) {
        return ResponseEntity.ok(jugadorService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JugadorResponse> update(@PathVariable Long id, @RequestBody JugadorRequest req) {
        return jugadorService.update(id, req)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return jugadorService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ─── Relaciones ──────────────────────────────────────────────────────────

    @PostMapping("/{id}/amigos/{amigoId}")
    public ResponseEntity<Map<String, String>> addAmigo(@PathVariable Long id, @PathVariable Long amigoId) {
        jugadorService.addAmigo(id, amigoId);
        return ResponseEntity.ok(Map.of("message", "Amistad creada"));
    }

    @DeleteMapping("/{id}/amigos/{amigoId}")
    public ResponseEntity<Map<String, String>> removeAmigo(@PathVariable Long id, @PathVariable Long amigoId) {
        jugadorService.removeAmigo(id, amigoId);
        return ResponseEntity.ok(Map.of("message", "Amistad eliminada"));
    }

    @PostMapping("/{id}/enemigos/{enemigoId}")
    public ResponseEntity<Map<String, String>> addEnemigo(@PathVariable Long id, @PathVariable Long enemigoId) {
        jugadorService.addEnemigo(id, enemigoId);
        return ResponseEntity.ok(Map.of("message", "Enemistad creada"));
    }

    @DeleteMapping("/{id}/enemigos/{enemigoId}")
    public ResponseEntity<Map<String, String>> removeEnemigo(@PathVariable Long id, @PathVariable Long enemigoId) {
        jugadorService.removeEnemigo(id, enemigoId);
        return ResponseEntity.ok(Map.of("message", "Enemistad eliminada"));
    }

    @PostMapping("/{id}/clan/{clanId}")
    public ResponseEntity<Map<String, String>> asignarClan(@PathVariable Long id, @PathVariable Long clanId) {
        jugadorService.asignarClan(id, clanId);
        return ResponseEntity.ok(Map.of("message", "Clan asignado"));
    }

    @DeleteMapping("/{id}/clan")
    public ResponseEntity<Map<String, String>> quitarClan(@PathVariable Long id) {
        jugadorService.quitarClan(id);
        return ResponseEntity.ok(Map.of("message", "Clan removido"));
    }
}
