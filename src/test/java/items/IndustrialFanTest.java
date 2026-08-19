package items;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import game.actors.Slime;
import game.items.IndustrialFan;
import game.inventory.BasicInventory;
import game.economy.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link IndustrialFan}: deposit reward, sell price,
 * HP-healing {@code onDeposit} side-effect, and Slime-spawning {@code onSold} side-effect.
 */
class IndustrialFanTest {

    private GameMap testMap;
    private ContractedWorker worker;

    /** Initialises a 7×7 floor map and places the worker at (3,3). */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(7, 7);
        worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        testMap.addActor(worker, testMap.at(3, 3));
    }

    /** Verifies that {@link IndustrialFan#getDepositReward()} returns 10. */
    @Test
    void depositRewardIs10() {
        assertEquals(10, new IndustrialFan().getDepositReward());
    }

    /** Verifies that {@link IndustrialFan#getSellPrice()} returns 150. */
    @Test
    void sellPriceIs150() {
        assertEquals(150, new IndustrialFan().getSellPrice());
    }

    /** Verifies that {@code onDeposit} heals the worker by exactly 10 HP. */
    @Test
    void onDepositHealsWorkerBy10() {
        // Hurt by 15 so there is room to heal 10 without hitting the max HP cap
        worker.hurt(15);
        int hpAfterHurt = worker.getStatistic(ActorStatistics.HEALTH);
        new IndustrialFan().onDeposit(worker, testMap.at(3, 3));
        int hpAfterDeposit = worker.getStatistic(ActorStatistics.HEALTH);
        assertEquals(hpAfterHurt + 10, hpAfterDeposit,
                "Depositing Industrial Fan should heal worker by 10 HP");
    }

    /** Verifies that {@code onSold} spawns a Slime on a tile adjacent to the supercomputer. */
    @Test
    void onSoldSpawnsSlimeAdjacentToSupercomputer() {
        // Use the terminal location as the supercomputer tile
        // Slime should appear on one of the adjacent Floor tiles
        new IndustrialFan().onSold(worker, testMap, testMap.at(3, 3), new Wallet());

        boolean slimeSpawned = false;
        for (Exit exit : testMap.at(3, 3).getExits()) {
            if (exit.getDestination().containsAnActor()
                    && exit.getDestination().getActor() instanceof Slime) {
                slimeSpawned = true;
                break;
            }
        }
        assertTrue(slimeSpawned, "Selling Industrial Fan should spawn a Slime adjacent to supercomputer");
    }

    /** Verifies that any actor can always deposit an IndustrialFan. */
    @Test
    void canAlwaysBeDeposited() {
        assertTrue(new IndustrialFan().canBeDeposited(worker));
    }
}
