package req1;

import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import game.grounds.Floor;
import game.items.AlienArtifact;
import testutil.TestWorld;
import game.inventory.BasicInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlienArtifactTest {

    private GameMap testMap;
    private ContractedWorker worker;

    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(20, 20);
        worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        testMap.addActor(worker, testMap.at(10, 10));
    }

    @Test
    void depositRewardIs100() {
        AlienArtifact artifact = new AlienArtifact();
        assertEquals(100, artifact.getDepositReward());
    }

    @Test
    void sellPriceIs200() {
        AlienArtifact artifact = new AlienArtifact();
        assertEquals(200, artifact.getSellPrice());
    }

    @Test
    void canAlwaysBeDeposited() {
        AlienArtifact artifact = new AlienArtifact();
        assertTrue(artifact.canBeDeposited(worker));
    }

    @Test
    void onDepositTeleportsWorkerToValidLocation() {
        AlienArtifact artifact = new AlienArtifact();
        worker.getInventory().add(artifact);

        artifact.onDeposit(worker, testMap.at(10, 10));

        // Worker should still be on the map after teleport
        assertTrue(testMap.contains(worker),
                "Worker must still be on the map after depositing AlienArtifact");

        // Worker should land on a valid (Floor) tile and check canActorEnter was satisfied
        // (the new location accepted the actor, so it must have been valid)
        assertInstanceOf(Floor.class, testMap.locationOf(worker).getGround(), "Worker should land on a Floor tile after teleport");
    }

    @Test
    void onDepositTeleportsToSomewhereOnMap() {
        AlienArtifact artifact = new AlienArtifact();
        worker.getInventory().add(artifact);

        // Run deposit many times to confirm worker always stays on map
        for (int i = 0; i < 10; i++) {
            artifact.onDeposit(worker, testMap.at(10, 10));
            assertTrue(testMap.contains(worker),
                    "Worker must remain on map after every deposit teleport");
        }
    }
}

