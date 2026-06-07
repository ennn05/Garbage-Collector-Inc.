package req4;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;
import game.grounds.CrackedFloor;
import game.grounds.Floor;
import game.inventory.BasicInventory;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {

    private static void seedFloorRandom(Floor floor, long seed) {
        try {
            Field randField = Floor.class.getDeclaredField("rand");
            randField.setAccessible(true);
            Random random = (Random) randField.get(floor);
            random.setSeed(seed);
        } catch (ReflectiveOperationException e) {
            fail("Unable to seed Floor randomness: " + e.getMessage());
        }
    }

    @Test
    void degradeCanKeepTheSameFloorWhenTheRandomRollIsInTheStableRange() {
        Floor floor = new Floor();
        seedFloorRandom(floor, 2L);

        assertSame(floor, floor.degrade(), "Seed 2 should keep the same Floor instance");
    }

    @Test
    void degradeCanProduceACrackedFloorWhenTheRandomRollIsInTheDegradationRange() {
        Floor floor = new Floor();
        seedFloorRandom(floor, 0L);

        assertInstanceOf(CrackedFloor.class, floor.degrade(), "Seed 0 should crack the floor");
    }

    @Test
    void onActorStepDoesNothingToTheLocationOrActor() throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        Location location = map.at(1, 1);
        Floor floor = new Floor();
        location.setGround(floor);

        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        map.addActor(worker, location);

        floor.onActorStep(location, worker);

        assertSame(floor, location.getGround(), "Floor should remain unchanged when stepped on");
        assertSame(worker, location.getActor(), "Actor should remain on the same tile");
    }

    @Test
    void floorDegradeSeed16ProducesCrack() throws Exception {
        Floor floor = new Floor();
        Field randField = Floor.class.getDeclaredField("rand");
        randField.setAccessible(true);
        Random r = (Random) randField.get(floor);
        r.setSeed(16L);

        assertInstanceOf(CrackedFloor.class, floor.degrade(), "Seed 16 should produce a cracked floor");
    }
}