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

The Eclipse Nebula facility's environmental control systems have partially failed, leaving the moon's interior exposed to real-world atmospheric anomalies beamed in from Earth via a damaged orbital relay. Every 15 game turns, the system contacts the **OpenWeatherMap API** to retrieve live weather data for the geographic zone the player currently occupies, then physically reshapes the facility in two layers: a **passive zone effect** (always active, unique to each region) and **threshold-triggered effects** (only when live data crosses a danger level).

---

### The Mechanics

The 60-column facility map is divided into three equal **weather zones** (columns 0–19, 20–39, 40–59), each linked to a real Earth city. As a worker walks across the map, the zone they occupy determines which city's live weather is fetched — making the API query dynamically driven by the player's x-coordinate (a game-state variable).

| x-coordinate range | Zone class | City | Coordinates |
|--------------------|------------|------|-------------|
| 0 – 19 | `AridZone` | Melbourne, Australia | −37.81°, 144.96° |
| 20 – 39 | `TemperateZone` | London, United Kingdom | 51.51°, −0.13° |
| 40 – 59 | `HumidZone` | Tokyo, Japan | 35.68°, 139.69° |

Each weather cycle has two phases:

#### Phase 1 — Passive zone effect (always applied, driven by `WeatherZone`)

Every zone always applies its own complex environmental transformation, regardless of what the live weather data says. These effects represent the permanent climatic character of each region.

**`AridZone` — Desiccation**
- Scans a 6-tile radius around the player for `Puddle` tiles. Converts up to 2 of them back to `Floor` (the dry heat evaporates standing moisture).
- Additionally, scans the same radius for `ToxicWaste` tiles. Each one has a 30% chance to be replaced with a `Puddle` (diluted but still present) — the extreme heat partially neutralises chemical runoff.
- Net effect: the map becomes progressively drier and slightly safer chemically, but `StormEffect` puddles are constantly being consumed, creating a race between weather events.

**`TemperateZone` — Accelerated Flora Growth**
- Scans a 6-tile radius around the player for `FleshySprout` ground tiles. Replaces up to 2 of them with `FleshySapling` (advancing the growth stage by one).
- Additionally, scans for `FleshySapling` tiles and replaces up to 1 with `FleshyMatureTree`.
- Net effect: the mild, moist climate accelerates the flora threat. In maps where `FleshySprout`s are densely seeded, this zone turns a distant danger into an urgent one much faster than the normal growth cycle.

**`HumidZone` — Fire Suppression and Fungal Amplification**
- Scans a 6-tile radius around the player for `Fire` ground tiles. For each one found (up to 2), the `Fire` object's wrapped inner ground is restored — extinguishing the fire entirely.
- Additionally, scans for `FungalGround` tiles and for each one (up to 2) calls `forceSpread()`, immediately expanding fungal coverage.
- Net effect: the tropical humidity eliminates fire as a protective barrier while simultaneously accelerating fungal growth, creating a uniquely dangerous dual threat in the Tokyo zone.

---

#### Phase 2 — Threshold-triggered effects (applied based on live weather data, driven by `WeatherEffect`)

After the passive zone effect runs, the parsed weather data is checked against danger thresholds. Multiple effects can trigger in the same cycle.

**`HeatwaveEffect`** (temperature ≥ 32 °C)
- Converts up to 3 random Floor tiles within a 4-tile radius of the player into `Fire` ground.
- Fire damages any actor who steps on it.

**`FungalBloomEffect`** (humidity ≥ 75 %)
- Forces every `FungalGround` tile on the current map to spread immediately, bypassing the normal 10-turn passive spread timer.

**`StormEffect`** (wind speed ≥ 8 m/s)
- Converts up to 3 random Floor tiles within a 5-tile radius of the player into `Puddle` ground.
- Puddles poison actors who drink from them (unless sterilised).

---

### The Request

**Dynamic URL (game-state variable: player x-coordinate)**

The active `WeatherZone` builds the API query. Each zone hardcodes its own city's coordinates, so the URL changes automatically as the player moves:

```
GET https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&units=metric&appid={KEY}
```

**Example — player at x = 35 (`TemperateZone`, London):**
```
GET https://api.openweathermap.org/data/2.5/weather?lat=51.51&lon=-0.13&units=metric&appid=***
```

**Example — player at x = 8 (`AridZone`, Melbourne):**
```
GET https://api.openweathermap.org/data/2.5/weather?lat=-37.81&lon=144.96&units=metric&appid=***
```

---

### The Schema

**Example JSON response from OpenWeatherMap (London, rainy evening):**

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
    "humidity": 82,
    "pressure": 1012
  },
  "wind": {
    "speed": 9.2,
    "deg": 220
  },
  "name": "London"
}
```

**Fields consumed by the game (parsed internally by `WeatherSystem`):**

| JSON path | Game variable | Threshold effect triggered |
|-----------|--------------|---------------------------|
| `main.temp` | `temperature` (°C) | `HeatwaveEffect` if ≥ 32 |
| `main.humidity` | `humidity` (%) | `FungalBloomEffect` if ≥ 75 |
| `wind.speed` | `windSpeed` (m/s) | `StormEffect` if ≥ 8 |
| `weather[0].main` | `condition` (String) | Reserved for future condition effects |

All other fields are ignored. If a field is missing or the API is unreachable, each value falls back to a safe default so the game remains playable offline.

---

### The Architecture

#### New Abstractions

| Name | Type | Description |
|------|------|-------------|
| `WeatherZone` | Abstract class | Represents a geographic climate region of the facility map. Declares three abstract methods: `String buildApiQuery(String apiKey)` (constructs the dynamic URL using this zone's city coordinates), `boolean containsPlayerX(int x)` (true when the player is in this zone's column range), and `void applyPassiveEffect(GameMap map, Location playerLocation)` (the complex, always-active terrain transformation unique to this climate). Each concrete subclass physically alters the game world in a way that is mechanically distinct from every other zone. |
| `WeatherEffect` | Interface | Contract for a single environmental consequence triggered when live weather data crosses a numeric threshold. Declares `void apply(GameMap, Location)`, `boolean shouldActivate(WeatherReport)`, and `String getEffectName()`. |

#### Concrete Classes (3 per abstraction, 6 total)

**`WeatherZone` subclasses (3) — passive climate effects:**

| Class | City / x-range | `applyPassiveEffect` — what it does to the map |
|-------|----------------|------------------------------------------------|
| `AridZone` | Melbourne, 0–19 | Converts up to 2 `Puddle` → `Floor` (evaporation) in radius 6; each `ToxicWaste` tile in radius has 30% chance to become `Puddle` (heat-diluted runoff). |
| `TemperateZone` | London, 20–39 | Advances up to 2 `FleshySprout` → `FleshySapling` and up to 1 `FleshySapling` → `FleshyMatureTree` in radius 6, accelerating the flora threat. |
| `HumidZone` | Tokyo, 40–59 | Extinguishes up to 2 `Fire` tiles in radius 6 (restores inner ground); calls `forceSpread()` on up to 2 `FungalGround` tiles in the same radius. |

**`WeatherEffect` implementations (3) — threshold-triggered effects:**

| Class | Threshold | `apply` — what it does to the map |
|-------|-----------|----------------------------------|
| `HeatwaveEffect` | temp ≥ 32 °C | Converts up to 3 `Floor` → `Fire` within radius 4 of player. |
| `FungalBloomEffect` | humidity ≥ 75 % | Calls `forceSpread()` on every `FungalGround` tile on the entire map. |
| `StormEffect` | wind ≥ 8 m/s | Converts up to 3 `Floor` → `Puddle` within radius 5 of player. |

#### Higher-Level Classes (2, depending only on abstractions)

| Class | Role | Abstractions depended on |
|-------|------|--------------------------|
| `WeatherSystem` | Receives `List<WeatherZone>` and `List<WeatherEffect>` in its constructor (never concrete types). Each cycle: (1) iterates `WeatherZone`s to find the active zone for the player's x, (2) calls `activeZone.buildApiQuery(apiKey)` to get the dynamic URL, (3) fetches and parses the JSON into a `WeatherReport`, (4) calls `activeZone.applyPassiveEffect()`, (5) iterates `WeatherEffect`s and applies those whose `shouldActivate()` returns true. | `WeatherZone`, `WeatherEffect` |
| `WeatherBehaviour` | `Behaviour<Actor, Action>` implementation. Counts game turns and every 15 turns scans the map for the first WORKER actor, captures their location, and returns a `WeatherAction`. Depends on `WeatherSystem` which in turn depends only on the two abstractions. | `WeatherZone`, `WeatherEffect` (via `WeatherSystem`) |

#### Supporting Classes

| Class | Role |
|-------|------|
| `WeatherReport` | Immutable snapshot (temperature, humidity, windSpeed, condition) built by `WeatherSystem` from the parsed JSON and consumed by `WeatherEffect.shouldActivate()`. |
| `WeatherAction` | `Action` returned by `WeatherBehaviour`. Its `execute()` delegates to `WeatherSystem.fetchAndApply()`, connecting the action pipeline to the API and both effect layers. |
| `WeatherController` | Invisible `Actor` (' ') placed on an exterior dirt tile. Hosts `WeatherBehaviour` and returns its action each turn, keeping the weather pipeline within the actor–behaviour–action engine pattern without modifying any existing class. |

#### SOLID Principles Applied

| Principle | Application |
|-----------|-------------|
| **S** — Single Responsibility | `WeatherZone` subclasses own geographic identity + passive effect only. `WeatherEffect` implementations own threshold logic + reactive effect only. `WeatherSystem` orchestrates; `WeatherBehaviour` schedules; `WeatherController` hosts. No class does more than one job. |
| **O** — Open/Closed | A fourth zone (e.g., `PolarZone` for Reykjavik) or a fourth effect (e.g., `BlizzardEffect`) can be added by writing one new class and injecting it in `EclipseNebula` — zero changes to `WeatherSystem`, `WeatherBehaviour`, or any existing zone/effect. |
| **L** — Liskov Substitution | Any `WeatherZone` or `WeatherEffect` can be replaced with another implementation without breaking `WeatherSystem`'s contract. |
| **I** — Interface Segregation | `WeatherZone` (abstract class) and `WeatherEffect` (interface) each expose only the methods their direct consumers need. |
| **D** — Dependency Inversion | `WeatherSystem` and `WeatherBehaviour` depend on `List<WeatherZone>` and `List<WeatherEffect>` injected at construction — never on `AridZone`, `TemperateZone`, `HumidZone`, `HeatwaveEffect`, etc. |
