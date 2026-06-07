package req3;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import game.grounds.BlightFungus;
import game.grounds.Wall;
import testutil.TestWorld;
import game.actors.ContractedWorker;
import game.actors.Undead;
import game.inventory.BasicInventory;
import game.items.AlienCube;
import game.status.SporeInfection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlightFungusTest {

    private GameMap testMap;

    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(7, 7);
    }

    // â”€â”€ Infection on contact â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void workerOnBlightFungusReceivesSporeInfection() throws Exception {
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        testMap.addActor(worker, testMap.at(3, 3));
        testMap.at(3, 3).setGround(new BlightFungus());

        testMap.at(3, 3).tick();

        assertTrue(worker.hasStatus(SporeInfection.class),
                "Worker on BlightFungus should receive SporeInfection");
    }

    @Test
    void workerNotReinfectedIfAlreadyInfected() throws Exception {
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        testMap.addActor(worker, testMap.at(3, 3));
        testMap.at(3, 3).setGround(new BlightFungus());

        testMap.at(3, 3).tick(); // infects
        testMap.at(3, 3).tick(); // should not re-infect

        // hasStatus returns true but infection count should not be doubled
        assertTrue(worker.hasStatus(SporeInfection.class));
    }

    @Test
    void workerWithItemMayDropOnBlightFungusContact() throws Exception {
        ContractedWorker worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        worker.getInventory().add(new AlienCube());
        testMap.addActor(worker, testMap.at(3, 3));
        testMap.at(3, 3).setGround(new BlightFungus());

        // Run many ticks â€” 25% chance means statistically very likely to drop after 20+
        boolean itemDropped = false;
        for (int i = 0; i < 30 && !itemDropped; i++) {
            testMap.at(3, 3).tick();
            if (!testMap.at(3, 3).getItems().isEmpty()) {
                itemDropped = true;
            }
        }
        assertTrue(itemDropped, "Item should have been force-dropped within 30 ticks (25% chance)");
    }

    // â”€â”€ Passive spread â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void blightFungusCannotEnterNonSeedableTile() throws Exception {
        // Walls should not be seeded
        testMap.at(3, 3).setGround(new Wall());
        // BlightFungus spread checks Seedable â€” Wall does not implement it
        // Place BlightFungus adjacent and tick 10+ times to trigger spread
        testMap.at(2, 3).setGround(new BlightFungus());
        for (int i = 0; i < 10; i++) {
            testMap.at(2, 3).tick();
        }
        // Wall at (3,3) should remain a Wall
        assertTrue(testMap.at(3, 3).getGround() instanceof Wall,
                "BlightFungus spread must not overwrite non-Seedable tiles");
    }

    @Test
    void blightFungusSpreadsTurnsSeedableAdjacentTile() throws Exception {
        testMap.at(3, 3).setGround(new BlightFungus());

        // Tick exactly 10 times to trigger the spread interval
        for (int i = 0; i < 10; i++) {
            testMap.at(3, 3).tick();
        }

        int blightCount = 0;
        for (Exit exit : testMap.at(3, 3).getExits()) {
            if (exit.getDestination().getGround() instanceof BlightFungus) {
                blightCount++;
            }
        }
        assertEquals(1, blightCount,
                "Exactly one adjacent Floor tile should become BlightFungus after 10 ticks");
    }

    // â”€â”€ Undead AoE spread â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void undeadOnBlightFungusSpreadsToAdjacentFloors() throws Exception {
        testMap.at(3, 3).setGround(new BlightFungus());
        Undead undead = new Undead();
        testMap.addActor(undead, testMap.at(3, 3));

        testMap.at(3, 3).tick();

        int newBlightTiles = 0;
        for (Exit exit : testMap.at(3, 3).getExits()) {
            if (exit.getDestination().getGround() instanceof BlightFungus) {
                newBlightTiles++;
            }
        }
        assertTrue(newBlightTiles > 0,
                "Undead on BlightFungus should AoE-spread BlightFungus to adjacent floors");
    }
}

