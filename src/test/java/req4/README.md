# REQ4 Unit Tests — Documentation

This document explains what the REQ4 unit tests cover, how they map to the project rubric, and how to run them deterministically.

Location
- Tests: `src/test/java/req4/`

Purpose
- These tests exercise the Shockwave / CrackableGround / RubbleField mechanics defined in `req4-proposal.md`.
- They are designed to be deterministic, isolated, and to include positive, boundary and negative cases so each requirement has at least three test values/cases.

Summary of behaviours tested
- Resonator activation and shockwave effects (TremorBeacon, QuakeCharge, ResonanceMallet)
- Terrain degradation states (Floor → CrackedFloor → RubbleField)
- Cracked-floor grace period and collapse behaviour
- Rubble behaviour: impassable, hole-filling spread, and item interactions
- Actor burial and mummification → timed Undead resurfacing
- Cross-component interactions: item scattering and cascading terrain changes

Mapping tests to functional behaviours (example)
- Floor / degradation: `FloorTest.java` — stable (no change), degrade -> `CrackedFloor`, seeded-case that produces crack (seed 16)
- Cracked floor / grace period: `CrackedFloorTest.java` — actor moves away (no collapse), actor stays (collapse & burial), shockwave immediate collapse
- Rubble / hole filling / burial: `RubbleFieldTest.java` — spread into Holes, immobility checks, burial and mummification + undead spawn after configured ticks
- Resonators & scattering: `TremorBeaconTest.java`, `QuakeChargeTest.java`, `ResonanceMalletTest.java` — radius cases (inside, edge, outside), item scattering, ground conversions

How the tests satisfy the rubric
- Meaningful coverage: tests target each REQ4 behaviour and multiple edge cases (radius boundary, seeded randomness, no-adjacent-ground cases).
- Purposeful tests: each test asserts one behaviour (clear names and expectations).
- Deterministic: randomness is controlled by seeding Random instances inside tests (see `FloorTest.java`).
- Isolation: tests use `TestWorld` helper to create minimal, deterministic maps and actors; no network/time/external state is required.
- Three-cases-per-requirement: for each functional requirement at least three distinct input/case variants are present (typical, boundary, invalid/negative). Where additional cases are desired, consider parameterised tests.

Determinism & fixtures — practical notes
- Where a production class uses Random to make decisions (e.g., `Floor.degrade()`), tests seed the Random instance using reflection before invoking the method. This avoids changing production APIs while making tests reproducible.
- Where time/ticks/cooldowns matter, tests explicitly simulate ticks rather than waiting on real time.
- Tests avoid side effects by creating transient `TestWorld` maps and actors, and by cleaning up local state in the test scope.

Execution — recommended (Maven, preferred)
- Run all tests:

```bash
mvn test
```

- Run only the REQ4 tests:

```bash
mvn -Dtest=req4.* test
```
