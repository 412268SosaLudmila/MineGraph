package com.minegraph.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GrafoData {

    private List<GrafoNodo> nodes;
    private List<GrafoArista> edges;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GrafoNodo {
        private String id;
        private String label;
        private String group;
        private Integer nivel;
        private Boolean online;
        private String clan;
        private Integer size;
        private String color;
        private String titulo;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GrafoArista {
        private String id;
        private String from;
        private String to;
        private String tipo;
        private String color;
        private Double width;
        private String label;
    }
}
