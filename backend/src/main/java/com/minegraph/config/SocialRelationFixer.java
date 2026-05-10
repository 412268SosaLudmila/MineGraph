package com.minegraph.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Order(2)
public class SocialRelationFixer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SocialRelationFixer.class);

    private final Neo4jClient neo4jClient;

    public SocialRelationFixer(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public void run(String... args) {
        long countAmigos = countRel("AMIGO_DE");
        if (countAmigos > 0) {
            log.info("=== SocialRelationFixer: relaciones ya existen (AMIGO_DE={}) ===", countAmigos);
            return;
        }

        log.info("=== SocialRelationFixer: creando relaciones sociales con Neo4jClient... ===");

        List<Long> ids = new ArrayList<>(
            neo4jClient.query("MATCH (j:Jugador) RETURN id(j) AS nid ORDER BY nid")
                .fetchAs(Long.class).mappedBy((t, r) -> r.get("nid").asLong()).all()
        );

        if (ids.isEmpty()) {
            log.warn("=== SocialRelationFixer: no hay jugadores, abortando ===");
            return;
        }

        int n = ids.size();
        Random rnd = new Random(42);

        List<Map<String, Object>> amigosPairs = new ArrayList<>();
        List<Map<String, Object>> enemigosPairs = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int numAmigos = rnd.nextInt(6) + 3;
            Set<Long> used = new HashSet<>();
            used.add(ids.get(i));
            for (int a = 0; a < numAmigos; a++) {
                Long target = ids.get(rnd.nextInt(n));
                if (used.add(target)) {
                    amigosPairs.add(Map.of("from", ids.get(i), "to", target));
                }
            }

            int numEnemigos = rnd.nextInt(3) + 1;
            for (int e = 0; e < numEnemigos; e++) {
                Long target = ids.get(rnd.nextInt(n));
                if (used.add(target)) {
                    enemigosPairs.add(Map.of("from", ids.get(i), "to", target));
                }
            }
        }

        neo4jClient.query(
            "UNWIND $pairs AS p " +
            "MATCH (j1:Jugador) WHERE id(j1) = p.from " +
            "MATCH (j2:Jugador) WHERE id(j2) = p.to " +
            "MERGE (j1)-[:AMIGO_DE]->(j2)")
            .bind(amigosPairs).to("pairs")
            .run();

        neo4jClient.query(
            "UNWIND $pairs AS p " +
            "MATCH (j1:Jugador) WHERE id(j1) = p.from " +
            "MATCH (j2:Jugador) WHERE id(j2) = p.to " +
            "MERGE (j1)-[:ENEMIGO_DE]->(j2)")
            .bind(enemigosPairs).to("pairs")
            .run();

        log.info("=== SocialRelationFixer: creadas {} AMIGO_DE y {} ENEMIGO_DE ===",
            countRel("AMIGO_DE"), countRel("ENEMIGO_DE"));
    }

    private long countRel(String type) {
        return neo4jClient.query("MATCH ()-[:`" + type + "`]->() RETURN COUNT(*) AS c")
            .fetchAs(Long.class).mappedBy((t, r) -> r.get("c").asLong())
            .one().orElse(0L);
    }
}
