# ⬡ MineGraph

Sistema de análisis de redes sociales para servidores Minecraft, desarrollado con **Neo4j**, **Spring Boot 3** y **Angular 17**.

> Trabajo Práctico Grupal N°2 — Bases de Datos II

---

## 📋 Requisitos previos

Antes de iniciar el proyecto, asegurate de tener instalado:

| Herramienta | Versión recomendada | Descarga |
|-------------|---------------------|----------|
| Java JDK | 21 | [adoptium.net](https://adoptium.net) |
| Maven | 3.9+ | [maven.apache.org](https://maven.apache.org) |
| Node.js | 18+ | [nodejs.org](https://nodejs.org) |
| Angular CLI | 17 | `npm install -g @angular/cli` |
| Neo4j Desktop 2 | 2.x | [neo4j.com/download](https://neo4j.com/download) |

---

## 🗄️ 1. Configurar Neo4j

### Crear la base de datos en Neo4j Desktop 2
1. Abrí **Neo4j Desktop 2**
2. Creá un nuevo proyecto y dentro de él un nuevo **DBMS** (base de datos)
3. Usá la contraseña: `minegraph_secure_pass_2024`
4. Iniciá el DBMS desde la interfaz de Neo4j Desktop

> ⚠️ Si usás una contraseña distinta, actualizala en `backend/src/main/resources/application.yml`

### Verificar que Neo4j está corriendo
Neo4j debe estar escuchando en:
- **Bolt:** `bolt://localhost:7687`
- **HTTP:** `http://localhost:7474`

---

## ⚙️ 2. Iniciar el Backend (Spring Boot)

Abrí una terminal en la carpeta `backend/`:

```bash
cd backend
mvn spring-boot:run
```

El backend arranca en **http://localhost:8080**

> La primera vez que inicia, el `DataInitializer` carga automáticamente:
> ~800 jugadores, 15 clanes, eventos, regiones y todas las relaciones sociales.
> Esto puede tardar unos segundos extra en el primer arranque.

### Verificar que el backend está corriendo
```bash
curl http://localhost:8080/actuator/health
# Respuesta esperada: {"status":"UP"}
# (o HTTP 401 si el endpoint está protegido — también indica que está corriendo)
```

---

## 🌐 3. Iniciar el Frontend (Angular)

Abrí **otra terminal** en la carpeta `frontend/`:

```bash
cd frontend

# Primera vez: instalar dependencias
npm install

# Iniciar la aplicación
ng serve
```

El frontend arranca en **http://localhost:4200**

---

## 🚀 Orden de inicio

```
1️⃣  Neo4j Desktop 2   →   iniciar el DBMS desde la app
2️⃣  Backend           →   cd backend && mvn spring-boot:run
3️⃣  Frontend          →   cd frontend && ng serve
```

> ⚠️ **Importante:** el backend falla si Neo4j no está corriendo primero.

---

## 🔑 Credenciales de acceso

| Campo | Valor |
|-------|-------|
| Usuario | `admin` |
| Contraseña | `admin123` |

---

## 🗂️ Estructura del proyecto

```
MineGraph/
├── backend/                        ← API REST (Spring Boot 3 + Java 21)
│   ├── src/main/java/com/minegraph/
│   │   ├── config/                 ← DataInitializer, SocialRelationFixer
│   │   ├── controller/             ← Endpoints REST
│   │   ├── entity/                 ← Nodos Neo4j (Jugador, Clan, Evento, Region)
│   │   ├── repository/             ← Consultas Cypher (@Query)
│   │   ├── service/                ← Lógica de negocio
│   │   └── security/               ← JWT Authentication
│   └── src/main/resources/
│       └── application.yml         ← Configuración Neo4j y servidor
│
├── frontend/                       ← SPA Angular 17
│   └── src/app/
│       ├── pages/
│       │   ├── dashboard/          ← Panel principal con estadísticas
│       │   ├── graph/              ← Grafo interactivo (vis.js)
│       │   ├── players/            ← Gestión y búsqueda de jugadores
│       │   ├── clans/              ← Rankings y estado de clanes
│       │   ├── economy/            ← Volumen de comercio
│       │   └── events/             ← Eventos del servidor
│       ├── core/
│       │   ├── models/             ← Interfaces TypeScript
│       │   └── services/           ← Llamadas a la API
│       └── layouts/                ← Navbar, Sidebar
│
└── presentacion.html               ← Documento de presentación (abrir en navegador)
```

---

## 🧩 Modelo de datos Neo4j

### Nodos

| Nodo | Descripción |
|------|-------------|
| `Jugador` | Jugador del servidor. Propiedades: nickname, nivel, kills, muertes, monedas, reputacion, estadoOnline, horasJugadas |
| `Clan` | Grupo de jugadores. Propiedades: nombre, tag, ranking, riqueza, victorias, derrotas, territoriosControlados |
| `Evento` | Evento del servidor (PvP, construcción, etc.). Propiedades: nombre, tipo, fecha, recompensa, activo |
| `Region` | Zona del mapa. Propiedades: nombre, tipo, peligrosidad, coordX, coordZ |

### Relaciones

| Relación | Entre | Propiedades |
|----------|-------|-------------|
| `AMIGO_DE` | Jugador → Jugador | — |
| `ENEMIGO_DE` | Jugador → Jugador | — |
| `COMERCIA_CON` | Jugador → Jugador | volumenTotal, cantidadTransacciones, ultimaFecha, itemMasComerciado |
| `PERTENECE_A` | Jugador → Clan | — |
| `ALIADO_DE` | Clan → Clan | — |
| `EN_GUERRA_CON` | Clan → Clan | fechaInicio, motivo, bajas, activa |
| `PARTICIPO_EN` | Jugador → Evento | — |

---

## 🔍 Consultas Cypher destacadas

**Camino más corto entre dos jugadores:**
```cypher
MATCH (origen:Jugador {nickname: "Steve"}), (destino:Jugador {nickname: "Alex"}),
      path = shortestPath((origen)-[:AMIGO_DE*]-(destino))
RETURN [n IN nodes(path) | n.nickname] AS camino, length(path) AS grados
```

**Amigos de amigos (sugerencias sociales):**
```cypher
MATCH (j:Jugador {nickname: "Steve"})-[:AMIGO_DE*2..2]->(amigo2:Jugador)
WHERE NOT (j)-[:AMIGO_DE]->(amigo2) AND j <> amigo2
RETURN DISTINCT amigo2.nickname LIMIT 20
```

**Top PvP por ratio K/D:**
```cypher
MATCH (j:Jugador) WHERE j.muertes > 0
WITH j, toFloat(j.kills) / toFloat(j.muertes) AS kd
ORDER BY kd DESC LIMIT 10
RETURN j.nickname, kd
```

---

## 🛠️ Tecnologías utilizadas

| Capa | Tecnología |
|------|-----------|
| Base de datos | Neo4j 2026 (Graph Database) |
| Backend | Spring Boot 3.2, Java 21, Spring Data Neo4j 7 |
| Seguridad | JWT (JSON Web Tokens) |
| Frontend | Angular 17, TypeScript |
| Visualización grafo | vis.js (network) |
| Gráficos | Chart.js |
| Estilos | SCSS, Tailwind CSS |

---

## ⚡ Solución de problemas

**El backend no conecta a Neo4j:**
- Verificá que Neo4j Desktop esté iniciado y el DBMS corriendo
- Revisá que el puerto 7687 esté libre: `netstat -an | findstr 7687`
- Confirmá la contraseña en `application.yml`

**El frontend no carga datos:**
- Verificá que el backend esté corriendo en el puerto 8080
- Revisá la consola del navegador (F12) para ver errores CORS o 401

**`ng serve` falla:**
- Ejecutá `npm install` primero para instalar dependencias
- Verificá que tengas Angular CLI: `ng version`
