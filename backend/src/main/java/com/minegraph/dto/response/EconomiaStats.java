package com.minegraph.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EconomiaStats {
    private Double volumenTotal;
    private Long totalTransacciones;
    private Double transaccionPromedio;
    private List<Map<String, Object>> topItems;
    private List<Map<String, Object>> topVendedores;
    private List<Map<String, Object>> volumenPorMercado;
    private List<Map<String, Object>> actividadPorTipo;
    private List<TransaccionResponse> ultimasTransacciones;
}
