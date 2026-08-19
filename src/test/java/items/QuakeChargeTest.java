package items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.GrenadeAction;
import game.actors.ContractedWorker;
import game.grounds.RubbleField;
import game.inventory.BasicInventory;
import game.items.BeaconDetonator;
import game.items.QuakeCharge;
import game.items.ResonanceMallet;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

class QuakeChargeTest {

    @Test
    void constructionExposesTheExpectedCombatAndPurchaseStats() {
        QuakeCharge quakeCharge = new QuakeCharge();

        assertEquals(55, quakeCharge.getPurchasePrice(), "Quake Charge should cost 55 credits");
        assertEquals(1, quakeCharge.getShockwaveRadius(), "Quake Charge should have a 1-tile blast radius");
        assertTrue(quakeCharge.toString().contains("Remaining Uses: 3"), "Quake Charge should start with three uses");
    }

    @Test
    void allowableActionsProvideTheFourCardinalThrowOptions() throws Exception {
        GameMap map = new TestWorld().createFloorMap(7, 7);
        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        map.addActor(worker, map.at(3, 3));
        QuakeCharge quakeCharge = new QuakeCharge();

        ActionList actions = quakeCharge.allowableActions(worker, map);

        assertEquals(4, actions.size(), "Quake Charge should offer four throw directions");
        for (int i = 0; i < actions.size(); i++) {
            assertInstanceOf(GrenadeAction.class, actions.get(i), "Each selectable action should throw the grenade");
        }
    }

    @Test
    void triggerShockwavePushesNearbyItemsButLeavesDistantItemsUntouched() throws Exception {
        GameMap map = new TestWorld().createFloorMap(5, 5);
        Location epicenter = map.at(2, 2);
        QuakeCharge quakeCharge = new QuakeCharge();
        epicenter.setGround(new RubbleField());
        map.at(2, 1).setGround(new RubbleField());

        Item epicenterItem = new BeaconDetonator();
        Item radiusItem = new ResonanceMallet();
        Item distantItem = new BeaconDetonator();
        epicenter.addItem(epicenterItem);
        map.at(2, 1).addItem(radiusItem);
        map.at(0, 0).addItem(distantItem);

        quakeCharge.triggerShockwave(epicenter, map);

        assertTrue(map.at(3, 2).getItems().contains(epicenterItem), "Item at the blast center should be pushed one tile away");
        assertTrue(map.at(2, 0).getItems().contains(radiusItem), "Item within blast radius should also be pushed away");
        assertTrue(map.at(0, 0).getItems().contains(distantItem), "Item outside the blast radius should remain untouched");
    }
}