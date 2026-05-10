package com.minegraph.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Node("Clan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Clan {

    @Id @GeneratedValue
    private Long id;

    @Property("nombre")
    private String nombre;

    @Property("tag")
    private String tag;

    @Property("ranking")
    private Integer ranking;

    @Property("riqueza")
    private Double riqueza;

    @Property("cantidadMiembros")
    private Integer cantidadMiembros;

    @Property("fechaCreacion")
    private LocalDateTime fechaCreacion;

    @Property("descripcion")
    private String descripcion;

    @Property("nivel")
    private Integer nivel;

    @Property("victorias")
    private Integer victorias;

    @Property("derrotas")
    private Integer derrotas;

    @Property("territoriosControlados")
    private Integer territoriosControlados;

    @Property("color")
    private String color;

    @Relationship(type = "ALIADO_DE", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<Clan> aliados = new ArrayList<>();

    @Relationship(type = "EN_GUERRA_CON", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<GuerraClanRel> guerras = new ArrayList<>();
}
