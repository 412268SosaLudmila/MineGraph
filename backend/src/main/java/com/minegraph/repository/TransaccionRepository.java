package com.minegraph.repository;

import com.minegraph.entity.Transaccion;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TransaccionRepository extends Neo4jRepository<Transaccion, Long> {

    List<Transaccion> findByVendedorNick(String vendedorNick);

    List<Transaccion> findByCompradorNick(String compradorNick);

    List<Transaccion> findAllByOrderByFechaDesc();

    // Volumen económico total
    @Query("MATCH (t:Transaccion) RETURN SUM(t.total) AS volumenTotal")
    Double findVolumenTotal();

    // Top items más comercializados
    @Query("""
        MATCH (t:Transaccion)
        WITH t.itemNombre AS item, COUNT(t) AS cantidad, SUM(t.total) AS volumen
        RETURN item, cantidad, volumen ORDER BY volumen DESC LIMIT $limit
        """)
    List<Map<String, Object>> findTopItems(@Param("limit") int limit);

    // Top vendedores
    @Query("""
        MATCH (t:Transaccion)
        WITH t.vendedorNick AS vendedor, COUNT(t) AS ventas, SUM(t.total) AS ingresos
        RETURN vendedor, ventas, ingresos ORDER BY ingresos DESC LIMIT $limit
        """)
    List<Map<String, Object>> findTopVendedores(@Param("limit") int limit);

    // Volumen por mercado
    @Query("""
        MATCH (t:Transaccion)
        WITH t.mercado AS mercado, SUM(t.total) AS volumen, COUNT(t) AS transacciones
        RETURN mercado, volumen, transacciones ORDER BY volumen DESC
        """)
    List<Map<String, Object>> findVolumenPorMercado();

    // Últimas transacciones
    @Query("""
        MATCH (t:Transaccion)
        RETURN t ORDER BY t.fecha DESC LIMIT $limit
        """)
    List<Transaccion> findUltimasTransacciones(@Param("limit") int limit);

    @Query("MATCH (t:Transaccion) RETURN COUNT(t) AS total")
    Long countTotal();

    // Actividad económica por tipo
    @Query("""
        MATCH (t:Transaccion)
        WITH t.tipo AS tipo, COUNT(t) AS cantidad, SUM(t.total) AS volumen
        RETURN tipo, cantidad, volumen
        """)
    List<Map<String, Object>> findActividadPorTipo();
}
