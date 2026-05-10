package com.minegraph.repository;

import com.minegraph.entity.Clan;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ClanRepository extends Neo4jRepository<Clan, Long> {

    Optional<Clan> findByNombre(String nombre);

    List<Clan> findAllByOrderByRankingAsc();

    // Clanes dominantes (por riqueza + miembros + victorias)
    @Query("""
        MATCH (c:Clan)
        WITH c, (c.riqueza * 0.4 + c.cantidadMiembros * 100 + c.victorias * 50) AS poder
        ORDER BY poder DESC LIMIT $limit
        RETURN c
        """)
    List<Clan> findClanesDominantes(@Param("limit") int limit);

    // Clanes con más guerras activas
    @Query("""
        MATCH (c:Clan)-[g:EN_GUERRA_CON {activa: true}]->(:Clan)
        WITH c, COUNT(g) AS guerrasActivas
        ORDER BY guerrasActivas DESC
        RETURN c
        """)
    List<Clan> findClanesEnGuerra();

    // Red de alianzas
    @Query("""
        MATCH (c:Clan)-[a:ALIADO_DE]->(aliado:Clan)
        RETURN c, a, aliado
        """)
    List<Map<String, Object>> findRedAlianzas();

    // Clanes por territorio
    @Query("""
        MATCH (c:Clan)
        RETURN c ORDER BY c.territoriosControlados DESC LIMIT $limit
        """)
    List<Clan> findTopPorTerritorio(@Param("limit") int limit);

    @Query("MATCH (c:Clan) RETURN COUNT(c) AS total")
    Long countTotal();

    // Detección de comunidades (clanes muy conectados entre sí)
    @Query("""
        MATCH (c1:Clan)-[:ALIADO_DE*1..2]-(c2:Clan)
        WHERE c1 <> c2
        WITH c1, COUNT(DISTINCT c2) AS conexiones
        ORDER BY conexiones DESC LIMIT 10
        RETURN c1
        """)
    List<Clan> findClanesNucleoComunidad();
}
