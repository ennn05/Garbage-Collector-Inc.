package items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.CrackedFloor;
import game.grounds.RubbleField;
import game.items.BeaconDetonator;
import game.items.QuakeCharge;
import game.items.ResonanceMallet;
import game.items.TremorBeacon;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

class TremorBeaconTest {

    @Test
    void constructionExposesTheExpectedShockwaveAndPurchaseStats() {
        TremorBeacon beacon = new TremorBeacon();
        TremorBeacon purchased = beacon.createPurchasedItem();

        assertEquals(10, beacon.getPurchasePrice(), "Tremor Beacon should cost 10 credits");
        assertEquals(2, beacon.getShockwaveRadius(), "Tremor Beacon should have a 2-tile shockwave radius");
        assertEquals(1, beacon.getShockwavePower(), "Tremor Beacon should have power 1");
        assertInstanceOf(TremorBeacon.class, purchased, "Purchased beacons should be new TremorBeacon instances");
        assertNotSame(beacon, purchased, "Purchased beacons should not reuse the same object");
    }

    @Test
    void triggerShockwavePushesItemsWithinRadiusTwoButLeavesFarItemsAlone() throws Exception {
        GameMap map = new TestWorld().createFloorMap(7, 7);
        Location epicenter = map.at(3, 3);
        TremorBeacon beacon = new TremorBeacon();

        Item centerItem = new BeaconDetonator();
        Item radiusItem = new QuakeCharge();
        Item distantItem = new ResonanceMallet();
        epicenter.addItem(centerItem);
        map.at(3, 1).addItem(radiusItem);
        map.at(0, 0).addItem(distantItem);

        beacon.triggerShockwave(epicenter, map);

        assertTrue(map.at(4, 3).getItems().contains(centerItem), "Epicenter items should be pushed away from the shockwave");
        assertTrue(map.at(3, 0).getItems().contains(radiusItem), "Items exactly two tiles away should also be pushed");
        assertTrue(map.at(0, 0).getItems().contains(distantItem), "Items outside the shockwave radius should stay put");
    }

    @Test
    void triggerShockwaveTurnsCrackedGroundIntoRubbleInsideTheAffectedArea() throws Exception {
        GameMap map = new TestWorld().createFloorMap(7, 7);
        Location epicenter = map.at(3, 3);
        TremorBeacon beacon = new TremorBeacon();
        epicenter.setGround(new CrackedFloor());
        map.at(5, 3).setGround(new CrackedFloor());
        map.at(6, 3).setGround(new CrackedFloor());

        beacon.triggerShockwave(epicenter, map);

        assertInstanceOf(RubbleField.class, epicenter.getGround(), "The epicenter cracked floor should collapse immediately");
        assertInstanceOf(RubbleField.class, map.at(5, 3).getGround(), "Cracked ground inside the radius should collapse to rubble");
        assertInstanceOf(CrackedFloor.class, map.at(6, 3).getGround(), "Tiles outside the radius should not be affected");
    }
}