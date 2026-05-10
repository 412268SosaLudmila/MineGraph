package com.minegraph.repository;

import com.minegraph.entity.Item;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends Neo4jRepository<Item, Long> {
    List<Item> findByTipo(String tipo);
    List<Item> findByRareza(String rareza);
}
