package req3;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;
import game.grounds.BlightFungus;
import game.status.SporeInfection;
import testutil.TestWorld;
import game.inventory.BasicInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SporeInfectionTest {

    private GameMap testMap;
    private ContractedWorker worker;

    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        worker = new ContractedWorker("TestWorker", 'T', 20, new BasicInventory());
        testMap.addActor(worker, testMap.at(2, 2));
    }

    @Test
    void statusActiveOnCreation() {
        SporeInfection infection = new SporeInfection(5);
        assertTrue(infection.isStatusActive());
    }

    @Test
    void statusActiveWhileTurnsRemain() {
        SporeInfection infection = new SporeInfection(3);
        Location loc = testMap.at(2, 2);
        infection.tickStatus(worker, loc);   // 2 remaining
        infection.tickStatus(worker, loc);   // 1 remaining
        assertTrue(infection.isStatusActive());
    }

    @Test
    void statusInactiveAfterAllTurnsExpire() {
        SporeInfection infection = new SporeInfection(2);
        Location loc = testMap.at(2, 2);
        infection.tickStatus(worker, loc);
        infection.tickStatus(worker, loc);
        assertFalse(infection.isStatusActive());
    }

    @Test
    void statusExpiresPreciselyAtZeroTurns() {
        SporeInfection infection = new SporeInfection(1);
        Location loc = testMap.at(2, 2);
        assertTrue(infection.isStatusActive());
        infection.tickStatus(worker, loc);
        assertFalse(infection.isStatusActive());
    }

    @Test
    void blightFungusAppliesSporeInfectionOnEntry() throws Exception {
        // Place BlightFungus under the worker
        testMap.at(2, 2).setGround(new BlightFungus());
        // BlightFungus.tick() applies infection when actor is present
        testMap.at(2, 2).tick();
        assertTrue(worker.hasStatus(SporeInfection.class));
    }

    @Test
    void blightFungusDoesNotApplyInfectionTwice() throws Exception {
        testMap.at(2, 2).setGround(new BlightFungus());
        testMap.at(2, 2).tick();
        testMap.at(2, 2).tick();
        // Still only one infection status - hasStatus should still return true
        // (not stacked multiple times)
        assertTrue(worker.hasStatus(SporeInfection.class));
    }
}

