# Custom REQ4 Feature: Shockwaves

## The Pitch

The **Shockwave** system is a terrain-degradation cascade mechanic where players use seismic resonators (remote-activated beacons, thrown grenades, and melee mallets) to trigger progressive ground transformation. Stepping on cracked terrain causes it to collapse into rubble, which then fills nearby chasms—creating dynamic, evolving hazards that reshape the map and force strategic navigation decisions.

## The Mechanics

### Resonator Activation Methods
- **TremorBeacon (Remote Control):** Single-use item that triggers a 2-tile radius shockwave from up to 5 tiles away using a BeaconDetonator.
- **QuakeCharge (Grenade Throw):** Consumable grenade (3 uses) thrown 3 tiles in a direction; on impact, creates 1-tile radius shockwave.
- **ResonanceMallet (Melee Strike):** Reusable item with 10-turn cooldown that triggers shockwave on a adjacent tile;

### Terrain Degradation States
- **Stable Floor + Shockwave:** ~50% chance to degrade to CrackedFloor.
- **CrackedFloor:** If an actor steps on it, a 1-turn grace period is triggered. Actor has exactly 1 turn to move away before it collapses into RubbleField.
- **CrackedFloor (1 tick after Grace Period):** If actor stays, grace expires and the floor collapses into RubbleField. This mummifies the actor in place, marking them as Buried.
- **CrackedFloor + Shockwave:** Transforms immediately to RubbleField.
- **RubbleField (Impassable):** Blocks movement like a wall. Each turn, spreads to fill adjacent Holes, replacing them with new RubbleFields.

### Buried Actor Resurfacing Cycle
- If an actor is caught in a collapsed RubbleField, that actor is marked as **Buried** (removed from active map turn flow).
- A burial timer is started for that actor.
- After **20 game ticks**, the buried actor mutates into an **Undead** and resurfaces on a nearby valid ground tile.

### Cross-Component Interactions
1. **Item Scattering:** Shockwaves push all items from impact tiles by one tile-distance.
2. **Cascading Terrain:** Cracked floors transform to rubble when stepped on, affecting pathfinding and forcing map navigation changes.
3. **Hole Filling:** RubbleFields gradually fill adjacent Holes by converting them into new RubbleFields each turn.
4. **Actor Burial:** Actors caught by collapse are buried and temporarily removed from normal map interaction.
5. **Timed Undead Resurfacing:** Buried actors re-emerge after 20 ticks as Undead on nearby ground.

## The Architecture

### Abstractions (2 New)
1. **`Resonator` (Interface)**
   - Contract: `triggerShockwave(Location epicenter, GameMap map)`, `getShockwaveRadius()`, `getShockwavePower()`
   - Activation method and radius varies.

2. **`CrackableGround` (Abstract Class)**
   - Contract: `abstract void onActorStep(Location, Actor)`, `getDegradationLevel()`, `abstract Ground degrade()`
   - Manages multi-state terrain transitions with grace period logic.

### Concrete Classes (6 Total)

| Class | Type | Abstractions Implemented |
|-------|------|--------------------|
| **TremorBeacon** | Item | Resonator |
| **QuakeCharge** | Item | Resonator |
| **ResonanceMallet** | Item | Resonator |
| **Floor** | Ground | CrackableGround |
| **CrackedFloor** | Ground | CrackableGround |
| **RubbleField** | Ground | CrackableGround |
