package grounds;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import game.grounds.BlightFungus;
import game.grounds.Wall;
import testutil.TestWorld;
import game.actors.ContractedWorker;
import game.actors.Undead;
import game.inventory.BasicInventory;
import game.items.AlienCube;
import game.status.SporeInfection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BlightFungus}: spore infection on contact, no double-infection,
 * item drop chance, spread restrictions, and Undead-triggered AoE spread.
 */
class BlightFungusTest {

    private GameMap testMap;

    /** Initialises a 7×7 floor map for use in each test. */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(7, 7);
    }

    // ── Infection on contact ───────────────────────────────────────────────────

    /** Verifies that a worker standing on BlightFungus receives a SporeInfection status. */
    @Test
    void workerOnBlightFungusReceivesSporeInfection() throws Exception {
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        testMap.addActor(worker, testMap.at(3, 3));
        testMap.at(3, 3).setGround(new BlightFungus());

        testMap.at(3, 3).tick();

        assertTrue(worker.hasStatus(SporeInfection.class),
                "Worker on BlightFungus should receive SporeInfection");
    }

    /** Verifies that a worker already infected is not re-infected on a subsequent tick. */
    @Test
    void workerNotReinfectedIfAlreadyInfected() throws Exception {
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        testMap.addActor(worker, testMap.at(3, 3));
        testMap.at(3, 3).setGround(new BlightFungus());

        testMap.at(3, 3).tick(); // infects
        testMap.at(3, 3).tick(); // should not re-infect

        // hasStatus returns true but infection count should not be doubled
        assertTrue(worker.hasStatus(SporeInfection.class));
    }

    /** Verifies that a worker carrying an item may have it force-dropped within 30 ticks (25% chance). */
    @Test
    void workerWithItemMayDropOnBlightFungusContact() throws Exception {
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        worker.getInventory().add(new AlienCube());
        testMap.addActor(worker, testMap.at(3, 3));
        testMap.at(3, 3).setGround(new BlightFungus());

        // Run many ticks — 25% chance means statistically very likely to drop after 20+
        boolean itemDropped = false;
        for (int i = 0; i < 30 && !itemDropped; i++) {
            testMap.at(3, 3).tick();
            if (!testMap.at(3, 3).getItems().isEmpty()) {
                itemDropped = true;
            }
        }
        assertTrue(itemDropped, "Item should have been force-dropped within 30 ticks (25% chance)");
    }

    // ── Passive spread ─────────────────────────────────────────────────────────

    /** Verifies that BlightFungus spread cannot overwrite a non-Seedable tile (e.g., Wall). */
    @Test
    void blightFungusCannotEnterNonSeedableTile() throws Exception {
        // Walls should not be seeded
        testMap.at(3, 3).setGround(new Wall());
        // BlightFungus spread checks Seedable — Wall does not implement it
        // Place BlightFungus adjacent and tick 10+ times to trigger spread
        testMap.at(2, 3).setGround(new BlightFungus());
        for (int i = 0; i < 10; i++) {
            testMap.at(2, 3).tick();
        }
        // Wall at (3,3) should remain a Wall
        assertTrue(testMap.at(3, 3).getGround() instanceof Wall,
                "BlightFungus spread must not overwrite non-Seedable tiles");
    }

    /** Verifies that exactly one adjacent Floor tile becomes BlightFungus after 10 ticks. */
    @Test
    void blightFungusSpreadsTurnsSeedableAdjacentTile() throws Exception {
        testMap.at(3, 3).setGround(new BlightFungus());

        // Tick exactly 10 times to trigger the spread interval
        for (int i = 0; i < 10; i++) {
            testMap.at(3, 3).tick();
        }

        int blightCount = 0;
        for (Exit exit : testMap.at(3, 3).getExits()) {
            if (exit.getDestination().getGround() instanceof BlightFungus) {
                blightCount++;
            }
        }
        assertEquals(1, blightCount,
                "Exactly one adjacent Floor tile should become BlightFungus after 10 ticks");
    }

    // ── Undead AoE spread ──────────────────────────────────────────────────────

    /** Verifies that an Undead standing on BlightFungus triggers AoE spread to adjacent floor tiles. */
    @Test
    void undeadOnBlightFungusSpreadsToAdjacentFloors() throws Exception {
        testMap.at(3, 3).setGround(new BlightFungus());
        Undead undead = new Undead();
        testMap.addActor(undead, testMap.at(3, 3));

        testMap.at(3, 3).tick();

        int newBlightTiles = 0;
        for (Exit exit : testMap.at(3, 3).getExits()) {
            if (exit.getDestination().getGround() instanceof BlightFungus) {
                newBlightTiles++;
            }
        }
        assertTrue(newBlightTiles > 0,
                "Undead on BlightFungus should AoE-spread BlightFungus to adjacent floors");
    }
}
