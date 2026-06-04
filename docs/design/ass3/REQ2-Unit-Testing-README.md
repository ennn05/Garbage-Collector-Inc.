# REQ2 Unit Testing README

## Purpose

This document explains how to run the unit tests for Assignment 3 REQ2: The Scrap Snatcher.

The test file is located at:

```
test/game/REQ2UnitTest.java
```

These tests verify the main behaviours introduced for REQ2:

* Scrap Snatcher creation, health, display character, and capabilities
* Loot explosion spawn effect
* Snatching depositable resources
* Rejecting non-depositable resources
* Parasite infection of Scrap Snatcher
* Infected Scrap Snatcher damage and behaviour switching
* Deprecated fleshy tree behaviour on the 99-Deprecated map
* Fleshy Monolith warp behaviour

## Test Structure

The tests use small deterministic map fixtures instead of the full game maps. This keeps the tests isolated, reproducible, and easy to understand.

The test class contains helper methods such as:

```
createTestMap(...)
createOpenThreeByThreeMap()
createOpenFiveByFiveMap()
createWorker()
```

It also uses a small stub class:

```
NonDepositableTestItem
```


## Typical, Edge, and Invalid Cases

The tests include typical, edge, and invalid cases where relevant.

Examples:

* Typical case: a Scrap Snatcher moves to a tile with an `AluminiumScrap` and snatches it.
* Edge case: loot explosion occurs at the corner of the map and only affects existing adjacent tiles.
* Invalid case: a Scrap Snatcher moves to a tile with a non-depositable item and does not pick it up.

## Determinism

REQ2 contains random behaviour, such as random movement, random loot type selection, and random warp destination selection.

The tests avoid relying on one exact random result. For example:

* Loot explosion tests check that adjacent tiles receive valid depositable resources, not a specific resource type.
* Snatching tests use constrained maps with only one valid movement direction.
* Monolith warp tests only check that the worker leaves the original occupied location, not the exact destination.
* Random growth probability is not tested by waiting for a random success, because that would make the test flaky.

This makes the tests reproducible and deterministic.


## Expected Result

All tests in `REQ2UnitTest` should pass.


## Notes

These tests do not modify the game engine and do not rely on network access, real time, external files, or manual input. They use small map fixtures and a simple stub to keep the tests isolated and reproducible.
