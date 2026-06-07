package req4;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.DetonateAction;
import game.actors.ContractedWorker;
import game.items.BeaconDetonator;
import game.inventory.BasicInventory;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

class BeaconDetonatorTest {

    @Test
    void purchasePriceAndBaseNameStayStable() {
        BeaconDetonator detonator = new BeaconDetonator();

        assertEquals(15, detonator.getPurchasePrice(), "Beacon detonator should cost 15 credits");
        assertEquals("Remote Detonator", detonator.toString(), "The item should keep its base name");
    }

    @Test
    void createPurchasedItemReturnsANewBeaconDetonator() {
        BeaconDetonator detonator = new BeaconDetonator();
        BeaconDetonator purchased = detonator.createPurchasedItem();

        assertInstanceOf(BeaconDetonator.class, purchased);
        assertNotSame(detonator, purchased, "Purchased items should be new instances");
    }

    @Test
    void allowableActionsContainsExactlyOneDetonateAction() throws Exception {
        GameMap map = new TestWorld().createFloorMap(5, 5);
        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        map.addActor(worker, map.at(2, 2));
        BeaconDetonator detonator = new BeaconDetonator();

        ActionList actions = detonator.allowableActions(worker, map);

        assertEquals(1, actions.size(), "Beacon detonator should only provide one action");
        assertInstanceOf(DetonateAction.class, actions.get(0), "The single action should detonate nearby beacons");
        assertTrue(actions.get(0).menuDescription(worker).contains("detonates all nearby beacons"));
    }
}