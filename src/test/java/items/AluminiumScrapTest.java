package items;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import game.inventory.BasicInventory;
import game.items.AluminiumScrap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AluminiumScrap}: deposit reward, depositability,
 * {@code onDeposit} message, and the probabilistic cut-damage side-effect.
 */
class AluminiumScrapTest {

    private GameMap testMap;
    private ContractedWorker worker;

    /** Initialises a 5×5 floor map and places the worker at (2,2). */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        worker = new ContractedWorker("Bob", 'B', 20, new BasicInventory());
        testMap.addActor(worker, testMap.at(2, 2));
    }

    /** Verifies that {@link AluminiumScrap#getDepositReward()} returns 50. */
    @Test
    void depositRewardIs50() {
        assertEquals(50, new AluminiumScrap().getDepositReward());
    }

    /** Verifies that any actor can always deposit an AluminiumScrap. */
    @Test
    void canAlwaysBeDeposited() {
        assertTrue(new AluminiumScrap().canBeDeposited(worker));
    }

    /** Verifies that {@code onDeposit} returns a non-null, non-empty result message. */
    @Test
    void onDepositReturnsMessage() {
        String result = new AluminiumScrap().onDeposit(worker, testMap.at(2, 2));
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /** Verifies that the 20% cut chance causes damage at least once within 50 deposits. */
    @Test
    void onDepositMayDealDamage() {
        // 20% chance per call; 50 runs is statistically near-certain to hit once
        boolean damageTaken = false;
        for (int i = 0; i < 50 && !damageTaken; i++) {
            ContractedWorker fresh = new ContractedWorker("W", 'W', 100, new BasicInventory());
            int hpBefore = fresh.getStatistic(ActorStatistics.HEALTH);
            new AluminiumScrap().onDeposit(fresh, testMap.at(2, 2));
            if (fresh.getStatistic(ActorStatistics.HEALTH) < hpBefore) damageTaken = true;
        }
        assertTrue(damageTaken, "20% cut chance should trigger within 50 deposits");
    }

    /** Verifies that when the cut damage triggers, it deals exactly 5 HP. */
    @Test
    void onDepositDamageIs5WhenTriggered() {
        // Run until a damage event occurs and verify the amount is exactly 5
        for (int i = 0; i < 200; i++) {
            ContractedWorker fresh = new ContractedWorker("W", 'W', 100, new BasicInventory());
            int hpBefore = fresh.getStatistic(ActorStatistics.HEALTH);
            new AluminiumScrap().onDeposit(fresh, testMap.at(2, 2));
            int diff = hpBefore - fresh.getStatistic(ActorStatistics.HEALTH);
            if (diff > 0) {
                assertEquals(5, diff, "Cut damage must be exactly 5");
                return;
            }
        }
    }
}
