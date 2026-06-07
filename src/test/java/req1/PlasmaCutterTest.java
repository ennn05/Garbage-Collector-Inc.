package req1;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;

import game.actors.ContractedWorker;
import game.enums.ItemStatistics;
import game.inventory.BasicInventory;
import game.items.PlasmaCutter;
import game.status.BurnStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PlasmaCutter}: purchase price, weight statistic,
 * and the damage + burn side-effects applied on purchase.
 */
class PlasmaCutterTest {

    private GameMap testMap;
    private ContractedWorker worker;

    /** Initialises a 5×5 floor map and places the worker at (2,2). */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        worker = new ContractedWorker("Bob", 'B', 50, new BasicInventory());
        testMap.addActor(worker, testMap.at(2, 2));
    }

    /** Verifies that {@link PlasmaCutter#getPurchasePrice()} returns 50. */
    @Test
    void purchasePriceIs50() {
        assertEquals(50, new PlasmaCutter().getPurchasePrice());
    }

    /** Verifies that the PlasmaCutter's weight statistic is 7. */
    @Test
    void weightIs7() {
        PlasmaCutter cutter = new PlasmaCutter();
        assertEquals(7, cutter.getStatistic(ItemStatistics.WEIGHT));
    }

    /** Verifies that {@code onPurchased} deals exactly 5 damage to the buyer. */
    @Test
    void onPurchasedDeals5Damage() {
        int hpBefore = worker.getStatistic(ActorStatistics.HEALTH);
        new PlasmaCutter().onPurchased(worker, testMap, testMap.at(2, 2), new game.economy.Wallet());
        assertEquals(hpBefore - 5, worker.getStatistic(ActorStatistics.HEALTH));
    }

    /** Verifies that {@code onPurchased} applies a BurnStatus to the buyer. */
    @Test
    void onPurchasedAppliesBurnStatus() {
        new PlasmaCutter().onPurchased(worker, testMap, testMap.at(2, 2), new game.economy.Wallet());
        assertTrue(worker.hasStatus(BurnStatus.class),
                "Worker should have BurnStatus after purchasing PlasmaCutter");
    }

    /** Verifies that the PlasmaCutter's display character is {@code '>'}. */
    @Test
    void displayCharIsArrow() {
        assertEquals('>', new PlasmaCutter().getDisplayChar());
    }
}
