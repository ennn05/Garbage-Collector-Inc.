# Feature Proposal

---

## REQ3: Fungal Bloom System

### The Pitch

A bio-organic fungal infection gradually colonises the facility floor, with three distinct fungal ground types that each transform the environment in unique ways. Workers and creatures can become unwilling vectors of the infection, spreading fungal growth across the map as they move. Players must manage the spread carefully or risk the facility becoming overrun and untraversable.

---

### The Mechanics

#### Fungal Ground Types

**SporeExplosion (`*`)**
- A volatile cluster of pressurised spores that detonates on contact.
- When any actor steps onto this tile, it immediately explodes:
  - Deals **5 damage** to every actor within a 2-tile radius (AoE damage).
  - Converts **all adjacent Floor tiles** to `BlightFungus` (AoE terrain transformation).
- After exploding, the tile converts itself back to a regular `Floor`.
- Can only re-appear if seeded by a `SporeCanister` or an `Undead` stepping on nearby `FungalGround`.

**BlightFungus (`β`)**
- A creeping, dark-green mould that slowly colonises the facility.
- **Passive spread:** Every 10 turns, converts **one randomly chosen adjacent Floor tile** into another `BlightFungus` tile.
- **On actor entry (two effects):**
  1. Applies `SporeInfection` status to the actor (see below).
  2. Force-drops **one random item** from the actor's inventory onto the current tile — the fungus grows into their equipment and roots it to the floor. If the actor carries no items, this effect is skipped.
- The two effects combined make BlightFungus both a spreading terrain hazard and an inventory threat.

**SporeColony (`§`)**
- A dense, impassable mass of interlocked fungal stalks. Blocks all actor movement (like a wall).
- Every 25 turns, if a worker is within its adjacent tiles, it **spawns a `ScrapSnatcher`** on a random valid adjacent tile (spawn effect applies).
- When cut with the `PlasmaCutter`: drops a `SporeCanister` on the current tile and converts itself to a regular `Floor`.
- Acts as both a barrier and a passive enemy generator, forcing the player to decide between cutting it (at the cost of producing a `SporeCanister` they may not want) or navigating around it.

---

#### SporeInfection Status (new)

- A completely new status implementing the engine `Status` interface.
- Applied to an actor when they step onto a `BlightFungus` tile.
- Lasts **5 turns**.
- Each tick: there is a **30% chance** the actor's current Floor tile is converted to `BlightFungus`.
- Makes infected actors unwilling spreaders of the fungal infection as they walk around the map.
- Used directly by `ContractedWorker`'s `SporeEmitter` implementation — the infection is what triggers their spore-emission behaviour each turn.

---

#### SporeEmitter Interface

Implemented by any entity capable of actively seeding new `FungalGround` tiles. Declares:
- `void emitSpores(Location source)` — transforms nearby Floor tiles into a `FungalGround` subtype.
- `Class<? extends FungalGround> getSporeType()` — returns which fungal type this entity seeds.

**SporeCanister (`¡`) — new item**
- A pressurised canister of fungal spores. Weight: 3.
- Cannot be purchased from the Supercomputer — the only way to obtain one is by cutting a `SporeColony` with the `PlasmaCutter`.
- Can be sold to the Supercomputer for 60 worker credits.
- When used via `EmitSporesAction`: the worker selects an adjacent Floor tile and seeds it with a chosen `FungalGround` subtype (`SporeExplosion`, `BlightFungus`, or `SporeColony`).
- **25% chance** of an AoE burst: all adjacent Floor tiles are also seeded with the same `FungalGround` type simultaneously.

**ContractedWorker — retrofitted existing class**
- Now implements `SporeEmitter`.
- When the worker is afflicted with `SporeInfection`, their `emitSpores()` is called each turn during `playTurn`.
- On each call: **30% chance** to convert the Floor tile beneath their feet to `BlightFungus`.
- The worker must actively manage their position and status — walking through `BlightFungus` whilst already infected rapidly accelerates the spread.

**Undead — retrofitted existing class**
- Now implements `SporeEmitter`.
- When an `Undead` moves onto **any** `FungalGround` tile, `emitSpores()` is immediately triggered.
- On trigger: **all adjacent empty Floor tiles** are converted to `BlightFungus` (AoE terrain transformation).
- Makes Undead highly dangerous in areas with existing fungal growth — a single Undead walking through a `BlightFungus` patch can rapidly expand it in all directions.

---

#### EmitSporesAction — new action

- Extends the engine `Action` class.
- Created by `SporeCanister` in its `allowableActions()` method when held by a worker.
- Presents the player with a menu to choose a target adjacent Floor tile and a `FungalGround` subtype to seed on it.
- Each action carries a `Supplier<FungalGround>` for the chosen type; the action itself handles the terrain transformation and rolls for the AoE burst chance.

---

### The Architecture

#### New Abstractions

| Name | Type | Description |
|------|------|-------------|
| `FungalGround` | Abstract class (extends `Ground`) | Base class for all fungal terrain tiles. Declares abstract methods for actor-entry effects and passive spreading behaviour. |
| `SporeEmitter` | Interface | Contract for entities that can actively seed `FungalGround` tiles. Declares `emitSpores()` and `getSporeType()`. |

#### Concrete Classes

| Class | Status | Abstraction | Complex Effect |
|-------|--------|-------------|----------------|
| `SporeExplosion` | **NEW** | Extends `FungalGround` | AoE damage to all actors in radius + AoE terrain conversion of adjacent tiles to `BlightFungus` + self-destruction |
| `BlightFungus` | **NEW** | Extends `FungalGround` | Controlled single-tile passive spread every 10 turns + applies `SporeInfection` status + force-drops item from inventory on entry |
| `SporeColony` | **NEW** | Extends `FungalGround` | Impassable barrier + periodic `ScrapSnatcher` spawn when worker nearby + drops `SporeCanister` and converts to Floor when cut |
| `SporeCanister` | **NEW** | Implements `SporeEmitter` | Player-targeted terrain transformation of any `FungalGround` type + 25% AoE burst converting all adjacent tiles simultaneously |
| `ContractedWorker` | **RETROFITTED** | Implements `SporeEmitter` | When infected with `SporeInfection`, each turn has 30% chance to convert current Floor tile to `BlightFungus` — infection turns the worker into a terrain-mutating vector |
| `Undead` | **RETROFITTED** | Implements `SporeEmitter` | Stepping onto any `FungalGround` tile immediately triggers AoE conversion of all adjacent empty Floor tiles to `BlightFungus` |

---

## REQ5: Real-World Weather System (A.P.I.)

### The Pitch

The Eclipse Nebula facility's environmental control systems have partially failed, leaving the moon's interior exposed to real-world atmospheric anomalies beamed in from Earth via a damaged orbital relay. Every 15 game turns, the system contacts the **OpenWeatherMap API** to retrieve live weather data for the city currently beneath the player on the orbital map, then physically reshapes the facility floor to match those conditions.

---

### The Mechanics

The 60-column facility map is divided into three equal **weather zones** (columns 0–19, 20–39, 40–59), each linked to a real Earth city. As a worker walks across the map, the zone they occupy determines which city's current weather is fetched — making the API query dynamically driven by the player's x-coordinate (a game-state variable).

| x-coordinate range | City | Coordinates |
|--------------------|------|-------------|
| 0 – 19 | Melbourne, Australia | −37.81°, 144.96° |
| 20 – 39 | London, United Kingdom | 51.51°, −0.13° |
| 40 – 59 | Tokyo, Japan | 35.68°, 139.69° |

Every 15 turns, three numerical fields are parsed from the JSON response and checked against independent thresholds to trigger one or more of the following physical effects:

**HeatwaveEffect** (temperature ≥ 32 °C)
- Converts up to 3 random Floor tiles within a 4-tile radius of the player into `Fire` ground.
- Fire damages any actor who steps on it and can spread to adjacent tiles.

**FungalBloomEffect** (humidity ≥ 75 %)
- Forces every `FungalGround` tile on the current map to spread immediately, bypassing the normal 10-turn passive spread timer.
- In maps already seeded with `BlightFungus` or `SporeColony`, this can cause a rapid, map-wide fungal surge.

**StormEffect** (wind speed ≥ 8 m/s)
- Scatters atmospheric moisture: converts up to 3 random Floor tiles within a 5-tile radius of the player into `Puddle` ground.
- Puddles poison actors who drink from them (unless sterilised), adding a lasting environmental hazard.

Multiple effects can trigger in the same weather cycle if multiple thresholds are exceeded simultaneously (e.g., a tropical thunderstorm could produce both high humidity and high wind speed).

---

### The Request

**Dynamic URL (game-state variable: player x-coordinate)**

```
GET https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&units=metric&appid={KEY}
```

Where `lat` and `lon` are selected based on `playerLocation.x()`:
- `playerX ∈ [0, 20)` → `lat=-37.81&lon=144.96` (Melbourne)
- `playerX ∈ [20, 40)` → `lat=51.51&lon=-0.13` (London)
- `playerX ∈ [40, 60)` → `lat=35.68&lon=139.69` (Tokyo)

**Example request for a player at x = 35 (London zone):**
```
GET https://api.openweathermap.org/data/2.5/weather?lat=51.51&lon=-0.13&units=metric&appid=***
```

---

### The Schema

**Example JSON response from OpenWeatherMap:**

```json
{
  "weather": [
    {
      "id": 500,
      "main": "Rain",
      "description": "light rain",
      "icon": "10d"
    }
  ],
  "main": {
    "temp": 14.3,
    "feels_like": 13.1,
    "temp_min": 12.0,
    "temp_max": 16.0,
    "pressure": 1012,
    "humidity": 82
  },
  "wind": {
    "speed": 9.2,
    "deg": 220
  },
  "name": "London"
}
```

**Fields consumed by the game:**

| JSON path | Extractor | Game use |
|-----------|-----------|----------|
| `main.temp` | `TemperatureExtractor` | Triggers `HeatwaveEffect` if ≥ 32 °C |
| `main.humidity` | `HumidityExtractor` | Triggers `FungalBloomEffect` if ≥ 75 % |
| `wind.speed` | `WindSpeedExtractor` | Triggers `StormEffect` if ≥ 8 m/s |
| `weather[0].main` | `WeatherConditionExtractor` | Reserved for future condition-based effects |

All other fields are ignored. Each extractor falls back to a safe default if its field is missing, so the game remains playable even when the API is unavailable.

---

### The Architecture

#### New Abstractions

| Name | Type | Description |
|------|------|-------------|
| `WeatherDataExtractor<T>` | Interface | Contract for parsing a single typed field out of the raw API JSON response. Declares `T extract(String json)` and `String getFieldName()`. |
| `WeatherEffect` | Interface | Contract for a single environmental consequence triggered by weather data. Declares `void apply(GameMap, Location)`, `boolean shouldActivate(WeatherReport)`, and `String getEffectName()`. |

#### Concrete Classes (≥ 3 per abstraction)

**WeatherDataExtractor implementations (4):**

| Class | Field parsed | Default |
|-------|-------------|---------|
| `TemperatureExtractor` | `main.temp` → `Double` | 20.0 °C |
| `HumidityExtractor` | `main.humidity` → `Integer` | 50 % |
| `WindSpeedExtractor` | `wind.speed` → `Double` | 5.0 m/s |
| `WeatherConditionExtractor` | `weather[0].main` → `String` | "Clear" |

**WeatherEffect implementations (3):**

| Class | Threshold | Physical game change |
|-------|-----------|----------------------|
| `HeatwaveEffect` | temp ≥ 32 °C | Up to 3 Floor → Fire within radius 4 |
| `FungalBloomEffect` | humidity ≥ 75 % | All FungalGround tiles spread immediately |
| `StormEffect` | wind ≥ 8 m/s | Up to 3 Floor → Puddle within radius 5 |

#### Higher-Level Classes (2, depending only on abstractions)

| Class | Role | Abstractions depended on |
|-------|------|--------------------------|
| `WeatherSystem` | Builds the API URL from the player's x-coordinate, fetches JSON via `HttpURLConnection`, runs all registered `WeatherDataExtractor`s to assemble a `WeatherReport`, then evaluates each `WeatherEffect` and applies those whose thresholds are met. Constructor accepts `List<WeatherDataExtractor<?>>` and `List<WeatherEffect>` — never concrete types. | `WeatherDataExtractor<?>`, `WeatherEffect` |
| `WeatherBehaviour` | `Behaviour<Actor, Action>` implementation. Counts game turns and, every 15 turns, scans the map for the first WORKER actor to retrieve their current location, then returns a `WeatherAction` carrying that location. Returns `DoNothingAction` on all other turns. Depends on `WeatherSystem` which in turn depends only on the two abstractions. | `WeatherEffect`, `WeatherDataExtractor<?>` (via `WeatherSystem`) |

#### Supporting Classes

| Class | Role |
|-------|------|
| `WeatherReport` | Immutable data snapshot (temperature, humidity, windSpeed, condition) assembled from extractor output and consumed by `WeatherEffect.shouldActivate()`. |
| `WeatherAction` | `Action` returned by `WeatherBehaviour`. Its `execute()` method calls `WeatherSystem.fetchAndApply()`, connecting the action pipeline to the API call. |
| `WeatherController` | Invisible `Actor` (' ', placed on an exterior dirt tile) whose sole purpose is to host `WeatherBehaviour` and return its action each turn. Keeps the weather pipeline entirely within the actor–behaviour–action engine pattern. |

#### SOLID Principles Applied

| Principle | Application |
|-----------|-------------|
| **S** — Single Responsibility | Each extractor parses exactly one JSON field; each effect triggers on exactly one threshold. `WeatherSystem` fetches and applies; `WeatherBehaviour` schedules; `WeatherController` hosts the behaviour. |
| **O** — Open/Closed | New weather effects or new data fields can be added by implementing `WeatherEffect` or `WeatherDataExtractor` and injecting the instance in `EclipseNebula` — no existing class needs to change. |
| **L** — Liskov Substitution | Any `WeatherEffect` implementation can be swapped in or out without changing `WeatherSystem`'s logic. |
| **I** — Interface Segregation | `WeatherDataExtractor` and `WeatherEffect` are small, focused interfaces with no unrelated methods. |
| **D** — Dependency Inversion | `WeatherSystem` and `WeatherBehaviour` depend only on the `WeatherDataExtractor` and `WeatherEffect` abstractions, injected at construction time in `EclipseNebula`. |
