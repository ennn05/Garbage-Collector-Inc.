# Garbage Collector Inc.

A Java text-based survival simulation built on a custom turn-based game engine. Contracted workers scavenge a derelict off-world salvage facility — managing resource quotas, mutated flora, a fungal infection that spreads across the map, seismic terrain hazards, and a shapeshifting enemy — while a live weather feed from Earth reshapes the facility in real time.

Built collaboratively by a 5-person team as a series of iterative feature additions to a shared engine.

## Overview

The player manages a crew of contracted workers salvaging scrap on the moon facilities **99-Deprecated** and **20-Overflow**. Each subsystem below was designed and added independently, each demonstrating a different design pattern on top of the same core actor/action/behaviour engine:

- **Economy & quotas** — workers sell scrap to a `Supercomputer` terminal against a rising quota, with consequences for falling behind.
- **Fungal infection system** — a bio-organic infection (`BlightFungus`, `SporeColony`, `SporeExplosion`) spreads across the map, infects actors, and turns them into unwitting vectors of further spread.
- **Shockwave / terrain degradation** — seismic resonators (remote beacon, thrown charge, melee mallet) crack and collapse floor tiles into impassable rubble, burying actors who linger and resurrecting them as `Undead` after a timer.
- **Mannequin state machine** — an adaptive enemy actor that cycles through four behavioural states (`Idle`, `Active`, `Berserk`, `Mimic`) based on worker proximity, inventory, and health.
- **Real-world weather system** — every 15 turns the game calls the **OpenWeatherMap REST API** for the live weather of a real city, mapped to the player's position on the map, and uses it to drive both constant per-zone terrain effects and threshold-triggered hazards (heatwaves, storms, fungal blooms).

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the full design write-up of the fungal, shockwave, and weather systems, including abstractions, class responsibilities, and how each applies SOLID principles.

## Design patterns & principles

| Pattern | Where |
|---|---|
| **Strategy** | `WeatherZone` / `WeatherEffect` / `WeatherDataExtractor` implementations are interchangeable, injected strategies for terrain effects, threshold reactions, and JSON parsing. |
| **State machine** | `game.states` (`IdleState`, `ActiveState`, `BerserkState`, `MimicState`) drives the Mannequin's behaviour via a `StateManager`. |
| **Dependency Inversion** | `WeatherSystem` and `WeatherBehaviour` depend only on the `WeatherZone`/`WeatherEffect` abstractions, injected via constructor — never on a concrete zone or effect class. |
| **Open/Closed** | New weather zones, weather effects, or fungal ground types can be added as a single new class with zero changes to the orchestrating classes. |

## Tech stack

- Java 17, Maven
- JUnit 5 (Jupiter) for unit testing
- `HttpURLConnection` for the OpenWeatherMap REST integration (no external HTTP library dependency)

## Getting started

```bash
mvn compile
mvn test
```

Run the game via `game.Application.main()` (e.g. from your IDE, or `mvn exec:java -Dexec.mainClass=game.Application`).

### Weather API key (optional)

The weather system works out of the box with safe default values (20 °C, 50% humidity, calm wind) if no API key is configured. To pull live weather data instead:

1. Get a free API key from [openweathermap.org/api](https://openweathermap.org/api) (new keys can take up to 2 hours to activate).
2. Provide it either as an environment variable:
   ```bash
   export OPENWEATHER_API_KEY=your_key_here
   ```
   or in a `.env` file in the project root:
   ```
   OPENWEATHER_API_KEY=your_key_here
   ```
   (`.env` is git-ignored — never commit your key.)

## Testing

177 JUnit unit tests covering economy, items, actions, behaviours, status effects, ground/terrain mechanics, flora, and the weather system, organised in [src/test/java](src/test/java) by feature area to mirror the main source layout. Tests use a `TestWorld` fixture to build minimal, deterministic maps and actors, and seed `Random` explicitly wherever production code relies on randomness, so runs are reproducible.

```bash
mvn test
```
