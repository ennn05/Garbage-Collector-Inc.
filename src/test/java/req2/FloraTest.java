package req2;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;
import game.flora.DeprecatedFleshyMatureTree;
import game.flora.DeprecatedFleshySprout;
import game.flora.FleshyMonolith;
import game.inventory.BasicInventory;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for deprecated flora ground tiles: {@link DeprecatedFleshySprout},
 * {@link DeprecatedFleshyMatureTree}, and {@link FleshyMonolith} tick behaviours.
 */
class FloraTest {

    // ── DeprecatedFleshySprout ─────────────────────────────────────────────────

    /** Verifies that a DeprecatedFleshySprout spawns an Undead on an adjacent tile when a worker is nearby. */
    @Test
    void sproutSpawnsUndeadOnAdjacentTileWhenWorkerIsNearby() throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        map.at(1, 1).setGround(new DeprecatedFleshySprout());

        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        map.addActor(worker, map.at(0, 1));

        map.at(1, 1).getGround().tick(map.at(1, 1));

        Location spawnTile = map.at(1, 0);
        assertTrue(spawnTile.containsAnActor(),
                "Sprout must spawn a creature on an adjacent tile when a worker is nearby");
        assertEquals('Ѫ', spawnTile.getActor().getDisplayChar(),
                "Spawned creature should be an Undead");
    }

    /** Verifies that a DeprecatedFleshySprout does not spawn anything when no worker is nearby. */
    @Test
    void sproutDoesNotSpawnWhenNoWorkerIsNearby() throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        map.at(1, 1).setGround(new DeprecatedFleshySprout());

        map.at(1, 1).getGround().tick(map.at(1, 1));

        for (Exit exit : map.at(1, 1).getExits()) {
            assertFalse(exit.getDestination().containsAnActor(),
                    "Sprout must not spawn anything when no worker is nearby");
        }
    }

    // ── DeprecatedFleshyMatureTree ─────────────────────────────────────────────

    /** Verifies that a DeprecatedFleshyMatureTree spawns a ScrapSnatcher on an adjacent tile when a worker is nearby. */
    @Test
    void matureTreeSpawnsScrapSnatcherWhenWorkerIsNearby() throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        map.at(1, 1).setGround(new DeprecatedFleshyMatureTree());

        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        map.addActor(worker, map.at(0, 1));

        map.at(1, 1).getGround().tick(map.at(1, 1));

        Location spawnTile = map.at(1, 0);
        assertTrue(spawnTile.containsAnActor(),
                "Mature tree must spawn a creature on an adjacent tile when a worker is nearby");
        assertEquals('s', spawnTile.getActor().getDisplayChar(),
                "Spawned creature should be a Scrap Snatcher");
    }

    // ── FleshyMonolith ─────────────────────────────────────────────────────────

    /** Verifies that a FleshyMonolith warps an adjacent worker to a different location on tick. */
    @Test
    void monolithWarpsAdjacentWorkerToDifferentLocation() throws Exception {
        GameMap map = new TestWorld().createFloorMap(5, 5);
        map.at(2, 2).setGround(new FleshyMonolith());

        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        Location original = map.at(1, 2);
        map.addActor(worker, original);

        map.at(2, 2).getGround().tick(map.at(2, 2));

        assertNotSame(original, map.locationOf(worker),
                "Monolith must warp an adjacent worker to a different tile");
    }

    /** Verifies that a FleshyMonolith does nothing when no worker is adjacent. */
    @Test
    void monolithDoesNothingWhenNoWorkerIsAdjacent() throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        map.at(1, 1).setGround(new FleshyMonolith());

        map.at(1, 1).getGround().tick(map.at(1, 1));

        for (Exit exit : map.at(1, 1).getExits()) {
            assertFalse(exit.getDestination().containsAnActor(),
                    "Monolith must not affect anything when no worker is adjacent");
        }
    }
}
