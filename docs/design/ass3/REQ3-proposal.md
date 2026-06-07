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
