# REQ2 Unit Tests — Documentation

This document mirrors the structure used for `req4` tests and explains what the REQ2 unit tests cover, how they map to the project rubric, and how to run them deterministically.

Location
- Tests: `src/test/java/req2/`

Purpose
- These tests exercise the gameplay mechanics covered by Requirement 2. They are written to be deterministic, isolated, and to include positive, boundary and negative cases so each requirement has at least three test values/cases.

Summary of behaviours typically tested in REQ2
- Flora growth and lifecycle (e.g., `FloraTest`)
- Creature/enemy status behaviours (e.g., `InfectedSnatcherStatusTest`)
- Explosive interactions and item behaviours (e.g., `LootExplosionTest`)
- Resource snatching/behaviour interactions (e.g., `SnatchDepositableResourceBehaviourTest`)

Mapping tests to functional behaviours (example)
- `req2/FloraTest.java` — growth, spread, death cases
- `req2/InfectedSnatcherStatusTest.java` — infection status transitions and edge cases
- `req2/LootExplosionTest.java` — explosion side effects on items/actors
- `req2/SnatchDepositableResourceBehaviourTest.java` — behaviour correctness and boundary conditions

How the tests satisfy the rubric
- Meaningful coverage: tests target each REQ2 behaviour and include boundary/negative cases.
- Purposeful tests: each test asserts a single behaviour with descriptive names.
- Deterministic: random or timing-based behaviours are seeded or simulated in tests.
- Isolation: tests use `TestWorld` or small deterministic fixtures; external systems are stubbed/mocked as needed.
- Three-cases-per-requirement: each requirement includes at least three distinct input/case variants (typical, boundary, invalid/negative).

Determinism & fixtures — practical notes
- Seed or stub randomness where necessary using reflection or injected RNGs.
- Simulate ticks explicitly for lifecycle or timed behaviours.
- Use local fixture builders (e.g., `TestWorld`) and mocks for complex dependencies.

Execution — recommended (Maven, preferred)
- Run all tests:

```bash
mvn test
```

- Run only the REQ2 tests:

```bash
mvn -Dtest=req2.* test
```
