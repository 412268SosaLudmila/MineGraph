package com.minegraph.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Node("Jugador")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Jugador {

    @Id @GeneratedValue
    private Long id;

    @Property("nickname")
    private String nickname;

    @Property("nivel")
    private Integer nivel;

    @Property("experiencia")
    private Long experiencia;

    @Property("fechaRegistro")
    private LocalDateTime fechaRegistro;

    @Property("estadoOnline")
    private Boolean estadoOnline;

    @Property("monedas")
    private Double monedas;

    @Property("reputacion")
    private Integer reputacion;

    @Property("kills")
    private Integer kills;

    @Property("muertes")
    private Integer muertes;

    @Property("horasJugadas")
    private Double horasJugadas;

    @Property("skin")
    private String skin;

    @Property("titulo")
    private String titulo;

    @Relationship(type = "AMIGO_DE", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<Jugador> amigos = new ArrayList<>();

    @Relationship(type = "ENEMIGO_DE", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<Jugador> enemigos = new ArrayList<>();

    @Relationship(type = "COMERCIA_CON", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<ComerciaConRel> comercioRelaciones = new ArrayList<>();

    @Relationship(type = "PERTENECE_A", direction = Relationship.Direction.OUTGOING)
    private Clan clan;

    @Relationship(type = "PARTICIPO_EN", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<Evento> eventosParticipados = new ArrayList<>();

    @Relationship(type = "VISITO", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<Region> regionesVisitadas = new ArrayList<>();
}
