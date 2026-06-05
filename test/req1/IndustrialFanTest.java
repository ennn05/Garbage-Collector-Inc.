package req1;

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

class IndustrialFanTest {

    private GameMap testMap;
    private ContractedWorker worker;

    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(7, 7);
        worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        testMap.addActor(worker, testMap.at(3, 3));
    }

    @Test
    void depositRewardIs10() {
        assertEquals(10, new IndustrialFan().getDepositReward());
    }

    @Test
    void sellPriceIs150() {
        assertEquals(150, new IndustrialFan().getSellPrice());
    }

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

    @Test
    void canAlwaysBeDeposited() {
        assertTrue(new IndustrialFan().canBeDeposited(worker));
    }
}

