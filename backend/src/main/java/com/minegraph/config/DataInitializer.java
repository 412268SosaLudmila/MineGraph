package com.minegraph.config;

import com.minegraph.entity.*;
import com.minegraph.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final JugadorRepository jugadorRepo;
    private final ClanRepository clanRepo;
    private final EventoRepository eventoRepo;
    private final RegionRepository regionRepo;
    private final TransaccionRepository transaccionRepo;

    private final Random rnd = new Random(42);

    public DataInitializer(JugadorRepository jugadorRepo,
                           ClanRepository clanRepo,
                           EventoRepository eventoRepo,
                           RegionRepository regionRepo,
                           TransaccionRepository transaccionRepo) {
        this.jugadorRepo = jugadorRepo;
        this.clanRepo = clanRepo;
        this.eventoRepo = eventoRepo;
        this.regionRepo = regionRepo;
        this.transaccionRepo = transaccionRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (jugadorRepo.count() > 0) {
            log.info("=== MineGraph: Datos ya inicializados ({} jugadores) ===", jugadorRepo.count());
            return;
        }
        log.info("=== MineGraph: Inicializando datos mock... ===");

        List<Region> regiones = crearRegiones();
        List<Evento> eventos = crearEventos(regiones);
        List<Clan> clanes = crearClanes();
        List<Jugador> jugadores = crearJugadores(clanes, regiones, eventos);
        crearRelacionesSociales(jugadores);
        crearRelacionesComerciales(jugadores);
        crearRelacionesClanes(clanes);
        crearTransacciones(jugadores);

        log.info("=== MineGraph: Datos inicializados: {} jugadores, {} clanes, {} eventos ===",
                jugadores.size(), clanes.size(), eventos.size());
    }

    private List<Region> crearRegiones() {
        String[][] regionData = {
            {"Valle del Dragón", "PELIGROSA", "9"},
            {"Bosque Élfico", "EXPLORACIÓN", "4"},
            {"Minas de Hierro", "MINERÍA", "6"},
            {"Ciudad Central", "HUB", "1"},
            {"Pantano Maldito", "PELIGROSA", "8"},
            {"Montañas del Norte", "EXPLORACIÓN", "7"},
            {"Mercado del Sur", "COMERCIO", "2"},
            {"Cavernas del Caos", "DUNGEON", "10"},
            {"Pradera Dorada", "EXPLORACIÓN", "3"},
            {"Torre del Mago", "ESPECIAL", "5"},
            {"Puerto Nebuloso", "COMERCIO", "2"},
            {"Ruinas Antiguas", "PELIGROSA", "8"},
            {"Desierto Ardiente", "EXPLORACIÓN", "6"},
            {"Fortaleza del Rey", "PVP", "9"},
            {"Aldea de Inicio", "SEGURA", "1"}
        };

        List<Region> regiones = new ArrayList<>();
        for (int i = 0; i < regionData.length; i++) {
            Region r = Region.builder()
                    .nombre(regionData[i][0])
                    .tipo(regionData[i][1])
                    .peligrosidad(Integer.parseInt(regionData[i][2]))
                    .visitasTotal((long)(rnd.nextInt(10000) + 500))
                    .coordX(rnd.nextInt(2000) - 1000)
                    .coordZ(rnd.nextInt(2000) - 1000)
                    .activa(true)
                    .propietario(i < 3 ? "Sistema" : null)
                    .build();
            regiones.add(regionRepo.save(r));
        }
        return regiones;
    }

    private List<Evento> crearEventos(List<Region> regiones) {
        String[] tipos = {"PVP", "BOSS", "RAID", "TORNEO", "EXPLORACIÓN"};
        String[][] nombres = {
            {"Batalla de la Fortaleza", "Arena de Campeones", "Duelo al Atardecer"},
            {"El Dragón Anciano", "Leviatán de las Profundidades", "Señor del Caos"},
            {"Raid a las Catacumbas", "Asalto a la Torre", "Conquista del Norte"},
            {"Gran Torneo de Verano", "Copa del Servidor", "Campeonato Elite"},
            {"Expedición al Misterio", "Búsqueda del Artefacto", "Misión Imposible"}
        };

        List<Evento> eventos = new ArrayList<>();
        for (int t = 0; t < tipos.length; t++) {
            for (int n = 0; n < nombres[t].length; n++) {
                int participantes = rnd.nextInt(80) + 5;
                Evento e = Evento.builder()
                        .nombre(nombres[t][n])
                        .tipo(tipos[t])
                        .descripcion("Evento épico de tipo " + tipos[t] + " en el servidor MineGraph.")
                        .fecha(LocalDateTime.now().minusDays(rnd.nextInt(90)))
                        .duracionMinutos(rnd.nextInt(180) + 30)
                        .participantes(participantes)
                        .recompensa(rnd.nextDouble() * 50000 + 1000)
                        .region(regiones.get(rnd.nextInt(regiones.size())).getNombre())
                        .ganador(null)
                        .activo(rnd.nextBoolean())
                        .build();
                eventos.add(eventoRepo.save(e));
            }
        }
        return eventos;
    }

    private List<Clan> crearClanes() {
        String[][] clanData = {
            {"Guardianes del Abismo", "GDA", "#6c63ff"},
            {"Hermandad de Hierro", "HdH", "#ff6b35"},
            {"Sombras Eternas", "SE", "#1a1a2e"},
            {"Los Inmortales", "LI", "#00d4ff"},
            {"Caballeros del Caos", "CdC", "#ff4757"},
            {"Legión Dorada", "LD", "#ffd700"},
            {"Aquila Rising", "AR", "#2ed573"},
            {"Dark Phoenix", "DP", "#ff6b6b"},
            {"Vanguardia del Norte", "VdN", "#5352ed"},
            {"Cruzados del Destino", "CdD", "#eccc68"},
            {"Almas Perdidas", "AP", "#747d8c"},
            {"Tormenta Roja", "TR", "#ff4444"},
            {"Orden del Cristal", "OdC", "#70a1ff"},
            {"Hijos del Trueno", "HdT", "#1e90ff"},
            {"Cenizas del Imperio", "CdI", "#a29bfe"}
        };

        List<Clan> clanes = new ArrayList<>();
        for (int i = 0; i < clanData.length; i++) {
            int miembros = rnd.nextInt(18) + 3;
            Clan c = Clan.builder()
                    .nombre(clanData[i][0])
                    .tag(clanData[i][1])
                    .ranking(i + 1)
                    .riqueza(rnd.nextDouble() * 500000 + 10000)
                    .cantidadMiembros(miembros)
                    .fechaCreacion(LocalDateTime.now().minusDays(rnd.nextInt(365) + 30))
                    .descripcion("Clan de élite " + clanData[i][0] + ". Unidos por gloria y poder.")
                    .nivel(rnd.nextInt(15) + 1)
                    .victorias(rnd.nextInt(200) + 10)
                    .derrotas(rnd.nextInt(100))
                    .territoriosControlados(rnd.nextInt(8) + 1)
                    .color(clanData[i][2])
                    .build();
            clanes.add(clanRepo.save(c));
        }
        return clanes;
    }

    private List<Jugador> crearJugadores(List<Clan> clanes, List<Region> regiones, List<Evento> eventos) {
        String[] prefijos = {"Dark", "Shadow", "Iron", "Storm", "Fire", "Neon", "Void", "Cyber",
                              "Blaze", "Frost", "Chaos", "Epic", "Ultra", "Phoenix", "Thunder",
                              "Crystal", "Steel", "Ghost", "Raven", "Wolf"};
        String[] sufijos = {"Slayer", "Knight", "Warrior", "Master", "Hunter", "Blade",
                             "Rage", "Fury", "Strike", "Lord", "King", "Dragon", "Ninja",
                             "Titan", "God", "Hero", "Legend", "Pro", "Elite", "Prime"};
        String[] titulos = {"El Conquistador", "Guardián de las Sombras", "Maestro del Caos",
                             "El Invicto", "Señor de la Guerra", "El Inmortal", "Rey del PvP",
                             "Comerciante de Élite", "Explorador Supremo", "El Leyenda"};

        List<Jugador> jugadores = new ArrayList<>();
        Set<String> nicknames = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            String nickname;
            do {
                nickname = prefijos[rnd.nextInt(prefijos.length)] + "_" +
                           sufijos[rnd.nextInt(sufijos.length)] + (rnd.nextInt(999) + 1);
            } while (nicknames.contains(nickname));
            nicknames.add(nickname);

            int nivel = rnd.nextInt(100) + 1;
            int kills = rnd.nextInt(2000);
            int muertes = rnd.nextInt(1500) + 1;

            Clan clan = clanes.get(i % clanes.size());

            Jugador j = Jugador.builder()
                    .nickname(nickname)
                    .nivel(nivel)
                    .experiencia((long)(nivel * 1000L + rnd.nextInt(5000)))
                    .fechaRegistro(LocalDateTime.now().minusDays(rnd.nextInt(500) + 1))
                    .estadoOnline(rnd.nextInt(100) < 30)
                    .monedas(rnd.nextDouble() * 100000 + 100)
                    .reputacion(rnd.nextInt(1000) - 200)
                    .kills(kills)
                    .muertes(muertes)
                    .horasJugadas(rnd.nextDouble() * 2000 + 10)
                    .skin("skin_" + (rnd.nextInt(20) + 1))
                    .titulo(titulos[rnd.nextInt(titulos.length)])
                    .clan(clan)
                    .regionesVisitadas(regiones.subList(0, rnd.nextInt(regiones.size()) + 1))
                    .eventosParticipados(eventos.subList(0, rnd.nextInt(Math.min(eventos.size(), 5))))
                    .build();

            jugadores.add(jugadorRepo.save(j));
        }
        return jugadores;
    }

    private void crearRelacionesSociales(List<Jugador> jugadores) {
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadorRepo.findById(jugadores.get(i).getId()).orElse(null);
            if (j == null) continue;

            // Amigos (3-8 por jugador)
            int numAmigos = rnd.nextInt(6) + 3;
            List<Jugador> nuevosAmigos = new ArrayList<>();
            Set<Long> yaAgregados = new HashSet<>();
            yaAgregados.add(j.getId());

            for (int a = 0; a < numAmigos; a++) {
                int idx = rnd.nextInt(jugadores.size());
                Jugador posibleAmigo = jugadores.get(idx);
                if (!yaAgregados.contains(posibleAmigo.getId())) {
                    nuevosAmigos.add(posibleAmigo);
                    yaAgregados.add(posibleAmigo.getId());
                }
            }
            j.setAmigos(nuevosAmigos);

            // Enemigos (1-3 por jugador)
            int numEnemigos = rnd.nextInt(3) + 1;
            List<Jugador> nuevosEnemigos = new ArrayList<>();
            for (int e = 0; e < numEnemigos; e++) {
                int idx = rnd.nextInt(jugadores.size());
                Jugador posibleEnemigo = jugadores.get(idx);
                if (!yaAgregados.contains(posibleEnemigo.getId())) {
                    nuevosEnemigos.add(posibleEnemigo);
                    yaAgregados.add(posibleEnemigo.getId());
                }
            }
            j.setEnemigos(nuevosEnemigos);

            jugadorRepo.save(j);
        }
    }

    private void crearRelacionesComerciales(List<Jugador> jugadores) {
        String[] items = {"Diamante", "Hierro", "Esmeralda", "Oro", "Netherite",
                           "Madera", "Piedra", "Comida", "Poción", "Armadura"};

        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadorRepo.findById(jugadores.get(i).getId()).orElse(null);
            if (j == null) continue;

            int numComercio = rnd.nextInt(5) + 2;
            List<ComerciaConRel> relaciones = new ArrayList<>();
            Set<Long> yaComercio = new HashSet<>();
            yaComercio.add(j.getId());

            for (int c = 0; c < numComercio; c++) {
                int idx = rnd.nextInt(jugadores.size());
                Jugador socio = jugadores.get(idx);
                if (!yaComercio.contains(socio.getId())) {
                    yaComercio.add(socio.getId());
                    ComerciaConRel rel = ComerciaConRel.builder()
                            .jugador(socio)
                            .volumenTotal(rnd.nextDouble() * 50000 + 500)
                            .cantidadTransacciones(rnd.nextInt(100) + 5)
                            .ultimaFecha(LocalDateTime.now().minusDays(rnd.nextInt(30)))
                            .itemMasComerciado(items[rnd.nextInt(items.length)])
                            .build();
                    relaciones.add(rel);
                }
            }
            j.setComercioRelaciones(relaciones);
            jugadorRepo.save(j);
        }
    }

    private void crearRelacionesClanes(List<Clan> clanes) {
        for (int i = 0; i < clanes.size(); i++) {
            Clan clan = clanRepo.findById(clanes.get(i).getId()).orElse(null);
            if (clan == null) continue;

            // Alianzas (1-3)
            List<Clan> aliados = new ArrayList<>();
            int numAliados = rnd.nextInt(3) + 1;
            for (int a = 0; a < numAliados; a++) {
                int idx = rnd.nextInt(clanes.size());
                Clan aliado = clanes.get(idx);
                if (aliado.getId() != null && !aliado.getId().equals(clan.getId())) {
                    aliados.add(aliado);
                }
            }
            clan.setAliados(aliados);

            // Guerras (0-2)
            List<GuerraClanRel> guerras = new ArrayList<>();
            int numGuerras = rnd.nextInt(3);
            String[] motivos = {"Territorio", "Recursos", "Honor", "Traición", "Conquista"};
            for (int g = 0; g < numGuerras; g++) {
                int idx = rnd.nextInt(clanes.size());
                Clan enemigo = clanes.get(idx);
                if (enemigo.getId() != null && !enemigo.getId().equals(clan.getId())) {
                    GuerraClanRel guerra = GuerraClanRel.builder()
                            .clan(enemigo)
                            .fechaInicio(LocalDateTime.now().minusDays(rnd.nextInt(60) + 1))
                            .motivo(motivos[rnd.nextInt(motivos.length)])
                            .bajas(rnd.nextInt(500) + 10)
                            .activa(rnd.nextBoolean())
                            .build();
                    guerras.add(guerra);
                }
            }
            clan.setGuerras(guerras);
            clanRepo.save(clan);
        }
    }

    private void crearTransacciones(List<Jugador> jugadores) {
        String[] items = {"Diamante", "Esmeralda", "Lingote de Hierro", "Lingote de Oro",
                           "Netherite", "Madera de Roble", "Piedra", "Pan", "Manzana Dorada",
                           "Armadura de Diamante", "Espada de Netherite", "Arco Encantado",
                           "Poción de Fuerza", "Poción de Velocidad", "Libro Encantado",
                           "Perla del Nether", "Ojo del Éter", "Tocón de Corcho"};
        String[] mercados = {"Mercado Central", "Bazar del Sur", "Feria del Norte",
                              "Mercado Negro", "Plaza del Comercio", "Almacén del Puerto"};
        String[] tipos = {"VENTA", "COMPRA", "SUBASTA", "INTERCAMBIO"};

        List<Transaccion> transacciones = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            int vendedorIdx = rnd.nextInt(jugadores.size());
            int compradorIdx = rnd.nextInt(jugadores.size());
            while (compradorIdx == vendedorIdx) compradorIdx = rnd.nextInt(jugadores.size());

            int cantidad = rnd.nextInt(64) + 1;
            double precio = rnd.nextDouble() * 500 + 1;

            Transaccion t = Transaccion.builder()
                    .vendedorNick(jugadores.get(vendedorIdx).getNickname())
                    .compradorNick(jugadores.get(compradorIdx).getNickname())
                    .itemNombre(items[rnd.nextInt(items.length)])
                    .cantidad(cantidad)
                    .precioUnitario(Math.round(precio * 100.0) / 100.0)
                    .total(Math.round(precio * cantidad * 100.0) / 100.0)
                    .fecha(LocalDateTime.now().minusDays(rnd.nextInt(90)).minusHours(rnd.nextInt(24)))
                    .mercado(mercados[rnd.nextInt(mercados.length)])
                    .tipo(tipos[rnd.nextInt(tipos.length)])
                    .build();
            transacciones.add(t);
        }
        transaccionRepo.saveAll(transacciones);
    }
}
