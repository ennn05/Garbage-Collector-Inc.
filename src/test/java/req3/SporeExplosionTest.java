package req3;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import game.grounds.BlightFungus;
import game.grounds.Floor;
import game.grounds.SporeExplosion;
import game.inventory.BasicInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SporeExplosion}: self-destruct to Floor on actor entry,
 * AoE damage, adjacent floor conversion to BlightFungus, and no-detonation without actor.
 */
class SporeExplosionTest {

    private GameMap testMap;

    /** Initialises a 7×7 floor map for use in each test. */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(7, 7);
    }

    /** Verifies that SporeExplosion converts to Floor after an actor steps on it. */
    @Test
    void selfDestructsToFloorWhenActorStepsOn() throws Exception {
        testMap.at(3, 3).setGround(new SporeExplosion());
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 50, new BasicInventory());
        testMap.addActor(worker, testMap.at(3, 3));

        testMap.at(3, 3).tick();

        assertTrue(testMap.at(3, 3).getGround() instanceof Floor,
                "SporeExplosion must convert to Floor after detonation");
    }

    /** Verifies that actors within the AoE radius take damage when SporeExplosion detonates. */
    @Test
    void dealsAoeDamageToActorsInRadius() throws Exception {
        testMap.at(3, 3).setGround(new SporeExplosion());
        ContractedWorker trigger = new ContractedWorker("Trigger", 'T', 50, new BasicInventory());
        ContractedWorker victim  = new ContractedWorker("Victim",  'V', 50, new BasicInventory());
        testMap.addActor(trigger, testMap.at(3, 3));
        testMap.addActor(victim,  testMap.at(4, 3)); // within radius 2

        int hpBefore = victim.getStatistic(ActorStatistics.HEALTH);
        testMap.at(3, 3).tick();

        assertTrue(victim.getStatistic(ActorStatistics.HEALTH) < hpBefore,
                "Actor within AoE radius must take damage from SporeExplosion");
    }

    /** Verifies that adjacent Floor tiles become BlightFungus after SporeExplosion detonates. */
    @Test
    void convertsAdjacentFloorToBlightFungus() throws Exception {
        testMap.at(3, 3).setGround(new SporeExplosion());
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 50, new BasicInventory());
        testMap.addActor(worker, testMap.at(3, 3));

        testMap.at(3, 3).tick();

        int blightCount = 0;
        for (Exit exit : testMap.at(3, 3).getExits()) {
            if (exit.getDestination().getGround() instanceof BlightFungus) blightCount++;
        }
        assertTrue(blightCount > 0,
                "Adjacent Floor tiles must become BlightFungus after explosion");
    }

    /** Verifies that SporeExplosion does not detonate when no actor is present on its tile. */
    @Test
    void doesNotDetonateWithoutActor() {
        testMap.at(3, 3).setGround(new SporeExplosion());
        testMap.at(3, 3).tick(); // no actor present
        assertTrue(testMap.at(3, 3).getGround() instanceof SporeExplosion,
                "SporeExplosion must not detonate when no actor is present");
    }
}
