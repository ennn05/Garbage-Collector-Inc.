package grounds;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;
import game.actors.Undead;
import game.grounds.Hole;
import game.grounds.RubbleField;
import game.inventory.BasicInventory;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

class RubbleFieldTest {

    @Test
    void buryRemovesTheActorAndReturnsTheExpectedMessage() throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        Location center = map.at(1, 1);
        RubbleField rubble = new RubbleField();
        center.setGround(rubble);
        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        map.addActor(worker, center);

        String message = rubble.bury(worker, map);

        assertFalse(map.contains(worker), "Buried actors should be removed from the active map");
        assertTrue(message.contains("buried under"), "Burial message should describe the burial");
        assertTrue(message.contains("Rubble"), "Burial message should mention the rubble field");
    }

    @Test
    void tickConvertsAdjacentHolesIntoRubble() throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        Location center = map.at(1, 1);
        center.setGround(new RubbleField());
        map.at(2, 1).setGround(new Hole());

        center.getGround().tick(center);

        assertInstanceOf(RubbleField.class, map.at(2, 1).getGround(), "Rubble should fill nearby holes");
    }

    @Test
    void buriedActorReemergesAsUndeadAfterTwentyTicksWhenASpawnTileExists() throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        Location center = map.at(1, 1);
        RubbleField rubble = new RubbleField();
        center.setGround(rubble);
        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        map.addActor(worker, center);

        rubble.bury(worker, map);
        for (int i = 0; i < 20; i++) {
            center.getGround().tick(center);
        }

        assertFalse(map.contains(worker), "The original buried worker should stay removed after mummification");

        boolean foundUndead = false;
        for (Location nearby : center.getNearbyLocations(1)) {
            if (nearby.containsAnActor() && nearby.getActor() instanceof Undead) {
                foundUndead = true;
                break;
            }
        }
        assertTrue(foundUndead, "A buried actor should respawn as an Undead on a nearby valid tile after 20 ticks");
    }

    @Test
    void buriedActorFailsToReemergeWhenNoValidSpawnTileExists() throws Exception {
        GameMap map = new TestWorld().createFloorMap(1, 1);
        Location center = map.at(0, 0);
        RubbleField rubble = new RubbleField();
        center.setGround(rubble);
        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        map.addActor(worker, center);

        rubble.bury(worker, map);
        for (int i = 0; i < 20; i++) {
            center.getGround().tick(center);
        }

        assertFalse(map.contains(worker), "The worker should remain removed even when no valid respawn tile exists");
        assertFalse(center.containsAnActor(), "No replacement actor should be spawned on a 1x1 rubble map");
    }
}