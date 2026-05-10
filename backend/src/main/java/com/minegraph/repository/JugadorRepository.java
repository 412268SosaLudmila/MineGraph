package com.minegraph.repository;

import com.minegraph.entity.Jugador;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface JugadorRepository extends Neo4jRepository<Jugador, Long> {

    Optional<Jugador> findByNickname(String nickname);

    List<Jugador> findByEstadoOnlineTrue();

    List<Jugador> findByNivelGreaterThanEqual(Integer nivel);

    // Jugadores con más conexiones (amigos + enemigos + comercios)
    @Query("""
        MATCH (j:Jugador)
        OPTIONAL MATCH (j)-[:AMIGO_DE]->(a:Jugador)
        OPTIONAL MATCH (j)-[:ENEMIGO_DE]->(e:Jugador)
        OPTIONAL MATCH (j)-[:COMERCIA_CON]->(cc:Jugador)
        WITH j, COUNT(DISTINCT a) + COUNT(DISTINCT e) + COUNT(DISTINCT cc) AS conexiones
        ORDER BY conexiones DESC LIMIT $limit
        OPTIONAL MATCH (j)-[cr:PERTENECE_A]->(clan:Clan)
        RETURN j, cr, clan
        """)
    List<Jugador> findMasConectados(@Param("limit") int limit);

    // Top comerciantes por volumen
    @Query("""
        MATCH (j:Jugador)-[r:COMERCIA_CON]->(:Jugador)
        WITH j, SUM(r.volumenTotal) AS volumen
        ORDER BY volumen DESC LIMIT $limit
        OPTIONAL MATCH (j)-[cr:PERTENECE_A]->(clan:Clan)
        RETURN j, cr, clan
        """)
    List<Jugador> findTopComerciantes(@Param("limit") int limit);

    // Amigos de amigos (segundo grado)
    @Query("""
        MATCH (j:Jugador {nickname: $nickname})-[:AMIGO_DE*2..2]->(amigo2:Jugador)
        WHERE NOT (j)-[:AMIGO_DE]->(amigo2) AND j <> amigo2
        WITH DISTINCT amigo2 LIMIT 20
        OPTIONAL MATCH (amigo2)-[cr:PERTENECE_A]->(clan:Clan)
        RETURN amigo2, cr, clan
        """)
    List<Jugador> findAmigosDeAmigos(@Param("nickname") String nickname);

    // Camino más corto entre dos jugadores
    @Query("""
        MATCH (origen:Jugador {nickname: $origen}), (destino:Jugador {nickname: $destino}),
              path = shortestPath((origen)-[:AMIGO_DE*]-(destino))
        RETURN [n IN nodes(path) | n.nickname] AS path
        """)
    List<Map<String, Object>> findShortestPath(@Param("origen") String origen, @Param("destino") String destino);

    // Jugadores por clan
    @Query("""
        MATCH (j:Jugador)-[cr:PERTENECE_A]->(clan:Clan {nombre: $nombreClan})
        RETURN j, cr, clan
        """)
    List<Jugador> findByClanNombre(@Param("nombreClan") String nombreClan);

    // Top PvP ranking
    @Query("""
        MATCH (j:Jugador)
        WHERE j.muertes > 0
        WITH j, toFloat(j.kills) / toFloat(j.muertes) AS kd
        ORDER BY kd DESC LIMIT $limit
        OPTIONAL MATCH (j)-[cr:PERTENECE_A]->(clan:Clan)
        RETURN j, cr, clan
        """)
    List<Jugador> findTopPvP(@Param("limit") int limit);

    // Jugadores influyentes (PageRank-like: más mencionados en relaciones)
    @Query("""
        MATCH (j:Jugador)
        OPTIONAL MATCH ()-[:AMIGO_DE]->(j)
        OPTIONAL MATCH ()-[:COMERCIA_CON]->(j)
        WITH j, COUNT(*) AS influencia
        ORDER BY influencia DESC LIMIT $limit
        OPTIONAL MATCH (j)-[cr:PERTENECE_A]->(clan:Clan)
        RETURN j, cr, clan
        """)
    List<Jugador> findMasInfluyentes(@Param("limit") int limit);

    // Recomendación de clanes para jugador
    @Query("""
        MATCH (j:Jugador {nickname: $nickname})-[:AMIGO_DE]->(amigo:Jugador)-[:PERTENECE_A]->(c:Clan)
        WHERE NOT (j)-[:PERTENECE_A]->(c)
        WITH c, COUNT(amigo) AS amigosEnClan
        RETURN c ORDER BY amigosEnClan DESC LIMIT 5
        """)
    List<Map<String, Object>> findClanesRecomendados(@Param("nickname") String nickname);

    // Estadísticas generales del dashboard
    @Query("MATCH (j:Jugador) RETURN COUNT(j) AS total")
    Long countTotal();

    @Query("MATCH (j:Jugador {estadoOnline: true}) RETURN COUNT(j) AS online")
    Long countOnline();

    // Actividad reciente
    @Query("""
        MATCH (j:Jugador)
        WITH j ORDER BY j.fechaRegistro DESC LIMIT $limit
        OPTIONAL MATCH (j)-[cr:PERTENECE_A]->(clan:Clan)
        RETURN j, cr, clan
        """)
    List<Jugador> findActividadReciente(@Param("limit") int limit);

    // Datos para grafo completo
    @Query("""
        MATCH (j:Jugador)
        OPTIONAL MATCH (j)-[r:AMIGO_DE]->(a:Jugador)
        OPTIONAL MATCH (j)-[e:ENEMIGO_DE]->(en:Jugador)
        OPTIONAL MATCH (j)-[com:COMERCIA_CON]->(co:Jugador)
        RETURN j, r, a, e, en, com, co LIMIT 200
        """)
    List<Map<String, Object>> findGrafoData();

    // Búsqueda por nickname parcial
    @Query("""
        MATCH (j:Jugador)
        WHERE j.nickname =~ $pattern
        WITH j LIMIT 20
        OPTIONAL MATCH (j)-[cr:PERTENECE_A]->(clan:Clan)
        RETURN j, cr, clan
        """)
    List<Jugador> searchByNickname(@Param("pattern") String pattern);
}
