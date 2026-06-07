package req3;

import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import game.grounds.Floor;
import game.grounds.SporeColony;
import game.interfaces.Cuttable;
import game.inventory.BasicInventory;
import game.items.PlasmaCutter;
import game.items.SporeCanister;
import game.actions.CutAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

class SporeColonyTest {

    private GameMap testMap;

    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(7, 7);
    }

    @Test
    void blocksAllActorMovement() {
        SporeColony colony = new SporeColony();
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 50, new BasicInventory());
        assertFalse(colony.canActorEnter(worker),
                "SporeColony must be impassable to all actors");
    }

    @Test
    void implementsCuttable() {
        assertTrue(new SporeColony() instanceof Cuttable,
                "SporeColony must implement Cuttable");
    }

    @Test
    void canBeCutReturnsTrue() {
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 50, new BasicInventory());
        assertTrue(new SporeColony().canBeCut(worker));
    }

    @Test
    void dropsSporeCanisterWhenCut() throws Exception {
        testMap.at(3, 3).setGround(new SporeColony());
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 50, new BasicInventory());
        testMap.addActor(worker, testMap.at(2, 3));
        worker.getInventory().add(new PlasmaCutter());

        Cuttable colony = (Cuttable) testMap.at(3, 3).getGround();
        new CutAction(colony, testMap.at(3, 3)).execute(worker, testMap);

        boolean hasCanister = testMap.at(3, 3).getItems().stream()
                .anyMatch(item -> item instanceof SporeCanister);
        assertTrue(hasCanister, "Cutting SporeColony must drop a SporeCanister");
    }

    @Test
    void becomesFloorWhenCut() throws Exception {
        testMap.at(3, 3).setGround(new SporeColony());
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 50, new BasicInventory());
        testMap.addActor(worker, testMap.at(2, 3));
        worker.getInventory().add(new PlasmaCutter());

        Cuttable colony = (Cuttable) testMap.at(3, 3).getGround();
        new CutAction(colony, testMap.at(3, 3)).execute(worker, testMap);

        assertTrue(testMap.at(3, 3).getGround() instanceof Floor,
                "SporeColony must become Floor after being cut");
    }
}

