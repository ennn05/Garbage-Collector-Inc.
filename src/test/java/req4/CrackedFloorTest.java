package req4;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;
import game.grounds.CrackedFloor;
import game.grounds.RubbleField;
import game.inventory.BasicInventory;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

class CrackedFloorTest {

    private static GameMap createMapWithCrackedFloorAndWorker(Location[] crackedLocationOut, ContractedWorker[] workerOut) throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        Location crackedLocation = map.at(1, 1);
        crackedLocation.setGround(new CrackedFloor());
        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        map.addActor(worker, crackedLocation);

        crackedLocationOut[0] = crackedLocation;
        workerOut[0] = worker;
        return map;
    }

    @Test
    void firstStepConsumesGracePeriodButKeepsTheTileCracked() throws Exception {
        Location[] crackedLocationOut = new Location[1];
        ContractedWorker[] workerOut = new ContractedWorker[1];
        GameMap map = createMapWithCrackedFloorAndWorker(crackedLocationOut, workerOut);
        Location crackedLocation = crackedLocationOut[0];
        ContractedWorker worker = workerOut[0];

        crackedLocation.getGround().tick(crackedLocation);

        assertInstanceOf(CrackedFloor.class, crackedLocation.getGround(), "First step should only consume the grace period");
        assertTrue(map.contains(worker), "The worker should still be on the map during the grace period");
    }

    @Test
    void secondStepCollapsesIntoRubbleAndBuriesTheActor() throws Exception {
        Location[] crackedLocationOut = new Location[1];
        ContractedWorker[] workerOut = new ContractedWorker[1];
        GameMap map = createMapWithCrackedFloorAndWorker(crackedLocationOut, workerOut);
        Location crackedLocation = crackedLocationOut[0];
        ContractedWorker worker = workerOut[0];

        crackedLocation.getGround().tick(crackedLocation);
        crackedLocation.getGround().tick(crackedLocation);

        assertInstanceOf(RubbleField.class, crackedLocation.getGround(), "The cracked floor should collapse on the second step");
        assertFalse(map.contains(worker), "The buried actor should be removed from the active map");
        assertFalse(crackedLocation.containsAnActor(), "No actor should remain on the collapsed tile");
    }

    @Test
    void tickWithoutAnActorLeavesTheTileCracked() throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        Location crackedLocation = map.at(1, 1);
        crackedLocation.setGround(new CrackedFloor());

        crackedLocation.getGround().tick(crackedLocation);

        assertInstanceOf(CrackedFloor.class, crackedLocation.getGround(), "Cracked floor should not collapse without an actor standing on it");
    }
}