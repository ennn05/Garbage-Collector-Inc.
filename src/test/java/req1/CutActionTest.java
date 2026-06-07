package req1;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import testutil.TestWorld;
import game.actors.ContractedWorker;
import game.grounds.AluminiumDoor;
import game.grounds.Floor;
import game.actions.CutAction;
import game.interfaces.Cuttable;
import game.inventory.BasicInventory;
import game.items.AlienCube;
import game.items.PlasmaCutter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CutActionTest {

    private GameMap testMap;
    private ContractedWorker worker;

    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        worker = new ContractedWorker("Bob", 'B', 50, new BasicInventory());
        testMap.addActor(worker, testMap.at(2, 2));
        worker.getInventory().add(new PlasmaCutter());
    }

    // â”€â”€ AluminiumDoor cut â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void cuttingAluminiumDoorTransformsGroundToFloor() {
        Location doorLocation = testMap.at(3, 2);
        doorLocation.setGround(new AluminiumDoor());

        CutAction action = new CutAction(
                (Cuttable) doorLocation.getGround(), doorLocation);
        action.execute(worker, testMap);

        assertTrue(doorLocation.getGround() instanceof Floor,
                "Ground should be Floor after cutting");
    }

    @Test
    void cuttingAluminiumDoorDropsAluminiumScrap() {
        Location doorLocation = testMap.at(3, 2);
        doorLocation.setGround(new AluminiumDoor());

        CutAction action = new CutAction(
                (Cuttable) doorLocation.getGround(), doorLocation);
        action.execute(worker, testMap);

        assertFalse(doorLocation.getItems().isEmpty(),
                "AluminiumScrap should be dropped at door location");
    }

    @Test
    void cuttingWithoutPlasmaCutterFails() throws Exception {
        ContractedWorker unarmedWorker = new ContractedWorker("Unarmed", 'U', 50, new BasicInventory());
        testMap.addActor(unarmedWorker, testMap.at(1, 2));

        Location doorLocation = testMap.at(2, 2);
        doorLocation.setGround(new AluminiumDoor());

        CutAction action = new CutAction(
                (Cuttable) doorLocation.getGround(), doorLocation);
        String result = action.execute(unarmedWorker, testMap);

        assertTrue(result.contains("does not have a Plasma Cutter"),
                "Should report missing Plasma Cutter");
        assertTrue(doorLocation.getGround() instanceof AluminiumDoor,
                "Door should remain unchanged");
    }

    // â”€â”€ AlienCube inventory cut â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void cuttingAlienCubeRemovesCubeFromInventory() {
        AlienCube cube = new AlienCube();
        worker.getInventory().add(cube);
        assertTrue(worker.getInventory().getItems().contains(cube));

        Location workerLoc = testMap.locationOf(worker);
        CutAction action = new CutAction(cube, workerLoc);
        action.execute(worker, testMap);

        assertFalse(worker.getInventory().getItems().contains(cube),
                "AlienCube must be removed from inventory after cutting");
    }

    @Test
    void cuttingAlienCubeDropsAlienArtifactOnGround() {
        AlienCube cube = new AlienCube();
        worker.getInventory().add(cube);

        Location workerLoc = testMap.locationOf(worker);
        CutAction action = new CutAction(cube, workerLoc);
        action.execute(worker, testMap);

        assertFalse(workerLoc.getItems().isEmpty(),
                "AlienArtifact should be dropped at worker's location");
        assertEquals("Alien Artifact",
                workerLoc.getItems().get(0).toString(),
                "Dropped item should be AlienArtifact");
    }

    @Test
    void cuttingAlienCubeAppliesPoisonToWorker() {
        AlienCube cube = new AlienCube();
        worker.getInventory().add(cube);

        Location workerLoc = testMap.locationOf(worker);
        CutAction action = new CutAction(cube, workerLoc);
        action.execute(worker, testMap);

        assertTrue(worker.hasStatus(game.status.PoisonStatus.class),
                "Worker should be poisoned after cutting AlienCube");
    }

    @Test
    void cuttingAlienCubeDoesNotTransformGround() {
        AlienCube cube = new AlienCube();
        worker.getInventory().add(cube);

        Location workerLoc = testMap.locationOf(worker);
        CutAction action = new CutAction(cube, workerLoc);
        action.execute(worker, testMap);

        assertTrue(workerLoc.getGround() instanceof Floor,
                "Ground under worker should remain Floor after cutting inventory item");
    }
}

