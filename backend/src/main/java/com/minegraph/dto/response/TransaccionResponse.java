package com.minegraph.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransaccionResponse {
    private Long id;
    private String vendedorNick;
    private String compradorNick;
    private String itemNombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double total;
    private LocalDateTime fecha;
    private String mercado;
    private String tipo;
}
