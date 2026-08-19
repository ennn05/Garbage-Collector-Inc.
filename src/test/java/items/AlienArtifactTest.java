package items;

import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import game.grounds.Floor;
import game.items.AlienArtifact;
import testutil.TestWorld;
import game.inventory.BasicInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AlienArtifact}: deposit reward, sell price,
 * depositability, and the teleport side-effect of {@code onDeposit}.
 */
class AlienArtifactTest {

    private GameMap testMap;
    private ContractedWorker worker;

    /** Initialises a 20×20 floor map and places the worker at (10,10). */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(20, 20);
        worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        testMap.addActor(worker, testMap.at(10, 10));
    }

    /** Verifies that {@link AlienArtifact#getDepositReward()} returns 100. */
    @Test
    void depositRewardIs100() {
        AlienArtifact artifact = new AlienArtifact();
        assertEquals(100, artifact.getDepositReward());
    }

    /** Verifies that {@link AlienArtifact#getSellPrice()} returns 200. */
    @Test
    void sellPriceIs200() {
        AlienArtifact artifact = new AlienArtifact();
        assertEquals(200, artifact.getSellPrice());
    }

    /** Verifies that any actor can always deposit an AlienArtifact. */
    @Test
    void canAlwaysBeDeposited() {
        AlienArtifact artifact = new AlienArtifact();
        assertTrue(artifact.canBeDeposited(worker));
    }

    /** Verifies that depositing teleports the worker to a valid Floor tile on the map. */
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

    /** Verifies that the worker remains on the map across 10 consecutive deposits. */
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
