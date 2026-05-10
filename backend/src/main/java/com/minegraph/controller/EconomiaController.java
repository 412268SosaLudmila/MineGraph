package com.minegraph.controller;

import com.minegraph.dto.response.EconomiaStats;
import com.minegraph.dto.response.TransaccionResponse;
import com.minegraph.service.EconomiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/economia")
public class EconomiaController {

    private final EconomiaService economiaService;

    public EconomiaController(EconomiaService economiaService) {
        this.economiaService = economiaService;
    }

    @GetMapping("/stats")
    public ResponseEntity<EconomiaStats> getStats() {
        return ResponseEntity.ok(economiaService.getStats());
    }

    @GetMapping("/transacciones")
    public ResponseEntity<List<TransaccionResponse>> getTransacciones(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(economiaService.getUltimas(limit));
    }
}
