package com.minegraph.service;

import com.minegraph.dto.response.EconomiaStats;
import com.minegraph.dto.response.TransaccionResponse;
import com.minegraph.entity.Transaccion;
import com.minegraph.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EconomiaService {

    private final TransaccionRepository transaccionRepository;

    public EconomiaService(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    public EconomiaStats getStats() {
        List<Transaccion> todas = transaccionRepository.findAll();
        double volumen = todas.stream().mapToDouble(Transaccion::getTotal).sum();
        long total = todas.size();

        return EconomiaStats.builder()
                .volumenTotal(volumen)
                .totalTransacciones(total)
                .transaccionPromedio(total > 0 ? volumen / total : 0.0)
                .topItems(calcTopItems(todas, 10))
                .topVendedores(calcTopVendedores(todas, 10))
                .volumenPorMercado(calcVolumenPorMercado(todas))
                .actividadPorTipo(calcActividadPorTipo(todas))
                .ultimasTransacciones(mapTransacciones(
                        todas.stream().sorted(Comparator.comparing(Transaccion::getFecha).reversed())
                             .limit(50).collect(Collectors.toList())))
                .build();
    }

    private List<Map<String, Object>> calcTopItems(List<Transaccion> txs, int limit) {
        return txs.stream()
                .collect(Collectors.groupingBy(Transaccion::getItemNombre,
                        Collectors.summarizingDouble(Transaccion::getTotal)))
                .entrySet().stream()
                .sorted(Comparator.comparingDouble((Map.Entry<String, DoubleSummaryStatistics> e) ->
                        e.getValue().getSum()).reversed())
                .limit(limit)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("item", e.getKey());
                    m.put("cantidad", e.getValue().getCount());
                    m.put("volumen", e.getValue().getSum());
                    return m;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> calcTopVendedores(List<Transaccion> txs, int limit) {
        return txs.stream()
                .collect(Collectors.groupingBy(Transaccion::getVendedorNick,
                        Collectors.summarizingDouble(Transaccion::getTotal)))
                .entrySet().stream()
                .sorted(Comparator.comparingDouble((Map.Entry<String, DoubleSummaryStatistics> e) ->
                        e.getValue().getSum()).reversed())
                .limit(limit)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("vendedor", e.getKey());
                    m.put("ventas", e.getValue().getCount());
                    m.put("ingresos", e.getValue().getSum());
                    return m;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> calcVolumenPorMercado(List<Transaccion> txs) {
        return txs.stream()
                .collect(Collectors.groupingBy(Transaccion::getMercado,
                        Collectors.summarizingDouble(Transaccion::getTotal)))
                .entrySet().stream()
                .sorted(Comparator.comparingDouble((Map.Entry<String, DoubleSummaryStatistics> e) ->
                        e.getValue().getSum()).reversed())
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("mercado", e.getKey());
                    m.put("volumen", e.getValue().getSum());
                    m.put("transacciones", e.getValue().getCount());
                    return m;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> calcActividadPorTipo(List<Transaccion> txs) {
        return txs.stream()
                .collect(Collectors.groupingBy(Transaccion::getTipo,
                        Collectors.summarizingDouble(Transaccion::getTotal)))
                .entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("tipo", e.getKey());
                    m.put("cantidad", e.getValue().getCount());
                    m.put("volumen", e.getValue().getSum());
                    return m;
                })
                .collect(Collectors.toList());
    }

    public List<TransaccionResponse> getUltimas(int limit) {
        return mapTransacciones(transaccionRepository.findUltimasTransacciones(limit));
    }

    private List<TransaccionResponse> mapTransacciones(List<Transaccion> lista) {
        return lista.stream().map(t -> TransaccionResponse.builder()
                .id(t.getId())
                .vendedorNick(t.getVendedorNick())
                .compradorNick(t.getCompradorNick())
                .itemNombre(t.getItemNombre())
                .cantidad(t.getCantidad())
                .precioUnitario(t.getPrecioUnitario())
                .total(t.getTotal())
                .fecha(t.getFecha())
                .mercado(t.getMercado())
                .tipo(t.getTipo())
                .build()
        ).collect(Collectors.toList());
    }

    public Long countTotal() {
        return transaccionRepository.countTotal();
    }

    public Double getVolumenTotal() {
        Double v = transaccionRepository.findVolumenTotal();
        return v != null ? v : 0.0;
    }
}
