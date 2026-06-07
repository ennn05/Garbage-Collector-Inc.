# REQ3 Unit Tests — Documentation

This document mirrors the structure used for `req4` tests and explains what the REQ3 unit tests cover, how they map to the project rubric, and how to run them deterministically.

Location
- Tests: `src/test/java/req3/`

Purpose
- These tests exercise the gameplay mechanics covered by Requirement 3. They are written to be deterministic, isolated, and to include positive, boundary and negative cases so each requirement has at least three test values/cases.

Summary of behaviours typically tested in REQ3
- Fungus and spore behaviours (e.g., `BlightFungusTest`, `SporeColonyTest`)
- Spore explosion and infection mechanics (`SporeExplosionTest`, `SporeInfectionTest`)
- Spread, lifecycle and interactions with actors/environment

Mapping tests to functional behaviours (example)
- `req3/BlightFungusTest.java` — fungus growth and interaction cases
- `req3/SporeColonyTest.java` — colony spread, boundary cases
- `req3/SporeExplosionTest.java`, `req3/SporeInfectionTest.java` — explosion side effects and infection state changes

How the tests satisfy the rubric
- Meaningful coverage: tests target each REQ3 behaviour and include positive, boundary and negative cases.
- Purposeful tests: each test asserts a single behaviour with clear naming.
- Deterministic: seeded randomness and explicit tick simulation where required.
- Isolation: tests use `TestWorld` helper and local fixtures; external dependencies are mocked/stubbed as needed.
- Three-cases-per-requirement: each requirement includes at least three distinct input/case variants (typical, boundary, invalid/negative).

Determinism & fixtures — practical notes
- Seed Random or inject deterministic RNGs where production code uses randomness.
- Simulate ticks/cycles explicitly for timed behaviours rather than relying on real time.

Execution — recommended (Maven, preferred)
- Run all tests:

```bash
mvn test
```

- Run only the REQ3 tests:

```bash
mvn -Dtest=req3.* test
```
