# REQ5 Unit Tests — Documentation

This document mirrors the structure used for `req4` tests and explains what the REQ5 unit tests cover, how they map to the project rubric, and how to run them deterministically.

Location
- Tests: `src/test/java/req5/`

Purpose
- These tests exercise the gameplay mechanics covered by Requirement 5. They are written to be deterministic, isolated, and to include positive, boundary and negative cases so each requirement has at least three test values/cases.

Summary of behaviours typically tested in REQ5
- Weather systems and effects (e.g., `WeatherEffectTest`)
- Extractor and zone mechanics (e.g., `WeatherExtractorTest`, `WeatherZoneTest`)
- Interaction between weather and actors/environment

Mapping tests to functional behaviours (example)
- `req5/WeatherEffectTest.java` — individual weather effect application and duration
- `req5/WeatherExtractorTest.java` — extractor behaviour, resource extraction edge cases
- `req5/WeatherZoneTest.java` — zone creation, propagation and actor interactions

How the tests satisfy the rubric
- Meaningful coverage: tests target each REQ5 behaviour and include boundary/negative cases.
- Purposeful tests: each test asserts a single behaviour with descriptive method names.
- Deterministic: randomness and timing are seeded or simulated in tests.
- Isolation: tests use `TestWorld` and small fixtures; use mocks/stubs for complex dependencies when needed.
- Three-cases-per-requirement: for each requirement there are at least three distinct input/case variants (typical, boundary, invalid/negative).

Determinism & fixtures — practical notes
- Seed randomness where applicable and simulate ticks explicitly for timed behaviours.
- Use small, deterministic fixtures to avoid external state and ensure reproducibility.

Execution — recommended (Maven, preferred)
- Run all tests:

```bash
mvn test
```

- Run only the REQ5 tests:

```bash
mvn -Dtest=req5.* test
```
