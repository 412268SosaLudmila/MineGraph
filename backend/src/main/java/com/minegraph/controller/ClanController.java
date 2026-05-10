package com.minegraph.controller;

import com.minegraph.dto.response.ClanResponse;
import com.minegraph.service.ClanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clanes")
public class ClanController {

    private final ClanService clanService;

    public ClanController(ClanService clanService) {
        this.clanService = clanService;
    }

    @GetMapping
    public ResponseEntity<List<ClanResponse>> findAll() {
        return ResponseEntity.ok(clanService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClanResponse> findById(@PathVariable Long id) {
        return clanService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/dominantes")
    public ResponseEntity<List<ClanResponse>> dominantes(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(clanService.findDominantes(limit));
    }

    @GetMapping("/en-guerra")
    public ResponseEntity<List<ClanResponse>> enGuerra() {
        return ResponseEntity.ok(clanService.findEnGuerra());
    }

    @GetMapping("/top/territorio")
    public ResponseEntity<List<ClanResponse>> topTerritorio(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(clanService.findTopPorTerritorio(limit));
    }

    @GetMapping("/comunidad")
    public ResponseEntity<List<ClanResponse>> nucleoComunidad() {
        return ResponseEntity.ok(clanService.findNucleoComunidad());
    }
}
