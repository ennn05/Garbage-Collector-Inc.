package items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.MalletAction;
import game.actors.ContractedWorker;
import game.items.BeaconDetonator;
import game.items.ResonanceMallet;
import game.inventory.BasicInventory;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;
class ResonanceMalletTest {

    @Test
    void constructionStartsReadyAndWithTheExpectedStats() {
        ResonanceMallet mallet = new ResonanceMallet();

        assertEquals(50, mallet.getPurchasePrice(), "The mallet should cost 50 credits");
        assertEquals(0, mallet.getShockwaveRadius(), "The mallet should only affect the epicenter tile");
        assertTrue(mallet.toString().contains("Cooldown: 10/10"), "The mallet should start ready to use");
    }

    @Test
    void allowableActionsDisappearWhileTheMalletIsOnCooldown() throws Exception {
        GameMap map = new TestWorld().createFloorMap(5, 5);
        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        map.addActor(worker, map.at(2, 2));
        ResonanceMallet mallet = new ResonanceMallet();

        ActionList readyActions = mallet.allowableActions(worker, map);
        mallet.applyCooldown();
        ActionList coolingActions = mallet.allowableActions(worker, map);

        assertEquals(4, readyActions.size(), "A ready mallet should offer four attack directions");
        assertInstanceOf(MalletAction.class, readyActions.get(0), "The ready actions should be mallet attacks");
        assertEquals(0, coolingActions.size(), "A cooled mallet should not offer any actions until it recharges");
    }

    @Test
    void tickRechargesTheMalletAndTriggerShockwavePushesEpicenterItems() throws Exception {
        GameMap map = new TestWorld().createFloorMap(5, 5);
        Location epiccenter = map.at(2, 2);
        ResonanceMallet mallet = new ResonanceMallet();
        Item epiccenterItem = new BeaconDetonator();
        Item distantItem = new BeaconDetonator();
        epiccenter.addItem(epiccenterItem);
        map.at(4, 4).addItem(distantItem);

        mallet.applyCooldown();
        mallet.tick(epiccenter, null);
        assertTrue(mallet.toString().contains("Cooldown: 1/10"), "Ticking while cooling down should recharge the mallet");

        mallet.triggerShockwave(epiccenter, map);

        assertTrue(map.at(3, 2).getItems().contains(epiccenterItem), "Epicenter items should be pushed one tile away by the mallet");
        assertTrue(map.at(4, 4).getItems().contains(distantItem), "Distant items should not be affected by the mallet's zero-radius shockwave");
    }
}