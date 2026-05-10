package com.minegraph.repository;

import com.minegraph.entity.Mercado;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MercadoRepository extends Neo4jRepository<Mercado, Long> {
    List<Mercado> findByActivoTrue();
}
