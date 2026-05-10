package com.minegraph.repository;

import com.minegraph.entity.Region;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends Neo4jRepository<Region, Long> {

    List<Region> findByActivaTrue();

    List<Region> findByTipo(String tipo);

    // Territorios más visitados
    @Query("""
        MATCH (r:Region)
        RETURN r ORDER BY r.visitasTotal DESC LIMIT $limit
        """)
    List<Region> findMasVisitadas(@Param("limit") int limit);

    // Zonas más peligrosas
    @Query("""
        MATCH (r:Region)
        RETURN r ORDER BY r.peligrosidad DESC LIMIT $limit
        """)
    List<Region> findMasPeligrosas(@Param("limit") int limit);
}
