package req1;

import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import game.actions.DepositItemAction;
import game.economy.QuotaSystem;
import game.grounds.Supercomputer;
import game.interfaces.Depositable;
import game.inventory.BasicInventory;
import game.items.AluminiumScrap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DepositItemAction}: inventory removal, quota progression,
 * failure when item is not carried, and menu description formatting.
 */
class DepositItemActionTest {

    private GameMap testMap;
    private ContractedWorker worker;
    private Supercomputer supercomputer;

    /** Initialises a 5×5 map, places the worker at (2,2), and sets up a Supercomputer at (2,1). */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        worker = new ContractedWorker("Bob", 'B', 50, new BasicInventory());
        testMap.addActor(worker, testMap.at(2, 2));
        supercomputer = new Supercomputer();
        testMap.at(2, 1).setGround(supercomputer);
        supercomputer.initialiseQuotaSystem(testMap.at(2, 1));
    }

    /** Verifies that executing a deposit removes the item from the worker's inventory. */
    @Test
    void depositRemovesItemFromInventory() {
        AluminiumScrap scrap = new AluminiumScrap();
        worker.getInventory().add(scrap);
        Depositable dep = scrap.asCapability(Depositable.class).orElse(null);

        new DepositItemAction(scrap, dep, testMap.at(2, 1)).execute(worker, testMap);

        assertFalse(worker.getInventory().getItems().contains(scrap),
                "Item must be removed from inventory after deposit");
    }

    /** Verifies that executing a deposit increases the quota progress by the item's deposit reward. */
    @Test
    void depositProgressesQuota() {
        AluminiumScrap scrap = new AluminiumScrap();
        worker.getInventory().add(scrap);
        Depositable dep = scrap.asCapability(Depositable.class).orElse(null);

        QuotaSystem qs = supercomputer.getQuotaSystem();
        int before = qs.getCurrentQuotaProgress();

        new DepositItemAction(scrap, dep, testMap.at(2, 1)).execute(worker, testMap);

        assertEquals(before + scrap.getDepositReward(), qs.getCurrentQuotaProgress(),
                "Quota progress must increase by the item's deposit reward");
    }

    /** Verifies that depositing an item not in inventory returns an appropriate failure message. */
    @Test
    void depositFailsIfItemNotInInventory() {
        AluminiumScrap scrap = new AluminiumScrap();
        Depositable dep = scrap.asCapability(Depositable.class).orElse(null);

        String result = new DepositItemAction(scrap, dep, testMap.at(2, 1))
                .execute(worker, testMap);

        assertTrue(result.contains("no longer carrying"),
                "Should report item not carried");
    }

    /** Verifies that the menu description includes the item's 50-credit deposit reward. */
    @Test
    void menuDescriptionShowsDepositReward() {
        AluminiumScrap scrap = new AluminiumScrap();
        Depositable dep = scrap.asCapability(Depositable.class).orElse(null);

        String menu = new DepositItemAction(scrap, dep, testMap.at(2, 1))
                .menuDescription(worker);

        assertTrue(menu.contains("50"), "Menu should show the 50-credit deposit reward");
    }
}
