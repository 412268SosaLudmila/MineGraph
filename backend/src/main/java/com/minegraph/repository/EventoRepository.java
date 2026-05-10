package com.minegraph.repository;

import com.minegraph.entity.Evento;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface EventoRepository extends Neo4jRepository<Evento, Long> {

    List<Evento> findByActivoTrue();

    List<Evento> findByTipo(String tipo);

    List<Evento> findAllByOrderByFechaDesc();

    // Eventos más frecuentes por tipo
    @Query("""
        MATCH (e:Evento)
        WITH e.tipo AS tipo, COUNT(e) AS cantidad
        RETURN tipo, cantidad ORDER BY cantidad DESC
        """)
    List<Map<String, Object>> findEventosFrecuentes();

    // Eventos más recientes
    @Query("""
        MATCH (e:Evento)
        RETURN e ORDER BY e.fecha DESC LIMIT $limit
        """)
    List<Evento> findEventosRecientes(@Param("limit") int limit);

    @Query("MATCH (e:Evento) RETURN COUNT(e) AS total")
    Long countTotal();

    @Query("""
        MATCH (e:Evento)
        WITH e.tipo AS tipo, SUM(e.recompensa) AS totalRecompensa
        RETURN tipo, totalRecompensa ORDER BY totalRecompensa DESC
        """)
    List<Map<String, Object>> findRecompensasPorTipo();
}
