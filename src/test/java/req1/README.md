# REQ1 Unit Tests — Documentation

This document mirrors the structure used for `req4` tests and explains what the REQ1 unit tests cover, how they map to the project rubric, and how to run them deterministically.

Location
- Tests: `src/test/java/req1/`

Purpose
- These tests exercise the gameplay mechanics covered by Requirement 1. They are written to be deterministic, isolated, and to include positive, boundary and negative cases so each requirement has at least three test values/cases.

Summary of behaviours typically tested in REQ1
- Item creation/interaction (e.g., Aluminium scrap, Alien artefacts)
- Actions related to items (e.g., CutAction, DepositItemAction)
- Specific item behaviours (e.g., PlasmaCutter, IndustrialFan)
- Economy/management systems (e.g., Wallet, QuotaSystem)

Mapping tests to functional behaviours (example)
- `req1/AlienArtifactTest.java`, `req1/AluminiumScrapTest.java` — tests for item creation, properties and interactions
- `req1/CutActionTest.java`, `req1/DepositItemActionTest.java` — action availability, success and failure cases, side effects
- `req1/IndustrialFanTest.java`, `req1/PlasmaCutterTest.java` — item-specific behaviour and cooldowns/uses
- `req1/QuotaSystemTest.java`, `req1/WalletTest.java` — economy, capacity, edge cases (full/empty)

How the tests satisfy the rubric
- Meaningful coverage: tests target each REQ1 behaviour and include boundary/negative cases.
- Purposeful tests: each test asserts a single behaviour with clear names.
- Deterministic: where randomness is involved tests seed Random or use deterministic helpers.
- Isolation: tests use `TestWorld` or small fixtures to avoid external state.
- Three-cases-per-requirement: for each functional requirement at least three distinct input/case variants are present (typical, boundary, invalid/negative).

Determinism & fixtures — practical notes
- Seed Random instances when necessary using reflection in tests (same approach used in the `req4` tests).
- Simulate ticks/cooldowns directly rather than relying on real time.
- Use local fixture builders (e.g., `TestWorld`) and mock/stub external collaborators where appropriate.

Execution — recommended (Maven, preferred)
- Run all tests:

```bash
mvn test
```

- Run only the REQ1 tests:

```bash
mvn -Dtest=req1.* test
```
