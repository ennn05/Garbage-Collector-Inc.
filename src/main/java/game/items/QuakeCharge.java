package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.GrenadeAction;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.grounds.CrackableGround;
import game.interfaces.Purchasable;
import game.interfaces.Resonator;

import java.util.ArrayList;
import java.util.List;

/**
 * A throwable grenade that degrades ground and pushes items within a shockwave radius on detonation.
 * <p>
 * QuakeCharge can be thrown up to {@value #THROW_RANGE} tiles in the four cardinal directions.
 * Thrown objects stop at the first blocking tile. Each throw degrades one durability charge; the item
 * is removed from inventory when all {@value #INITIAL_DURABILITY} uses are exhausted.
 * The shockwave radius is {@value #SHOCKWAVE_RADIUS}, affecting only the tile immediately surrounding
 * the blast point.
 * Purchase price: {@value #PURCHASE_PRICE} credits. Weight: {@value #WEIGHT}.
 */
public class QuakeCharge extends Item implements Purchasable, Resonator {
    private static final int INITIAL_DURABILITY = 3;
    private static final int PURCHASE_PRICE = 55;
    private static final int SHOCKWAVE_POWER = 1;
    private static final int SHOCKWAVE_RADIUS = 1;
    private static final int THROW_RANGE = 3;
    private static final int WEIGHT = 3;

    /**
     * Creates a new QuakeCharge item (display character: {@code @}) with
     * {@value #INITIAL_DURABILITY} uses.
     */
    public QuakeCharge() {
        super("Quake Grenade", '@');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.DURABILITY, new BaseStatistic(INITIAL_DURABILITY));
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * {@inheritDoc}
     *
     * @return a new QuakeCharge instance
     */
    @Override
    public QuakeCharge createPurchasedItem() {
        return new QuakeCharge();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@value #PURCHASE_PRICE}
     */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@value #SHOCKWAVE_POWER}
     */
    @Override
    public int getShockwavePower() {
        return SHOCKWAVE_POWER;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@value #SHOCKWAVE_RADIUS}
     */
    @Override
    public int getShockwaveRadius() {
        return SHOCKWAVE_RADIUS;
    }

    /**
     * {@inheritDoc}
     *
     * @param buyer the actor attempting to buy this item
     * @param map   the map where the transaction happens
     * @return a message explaining the buyer cannot afford the item
     */
    @Override
    public String onInsufficientCredits(Actor buyer, GameMap map) {
        String message = buyer + " cannot afford the Quake Charge (costs " + PURCHASE_PRICE + " credits).";
        System.out.println(message);
        return message;
    }

    /**
     * {@inheritDoc}
     *
     * @param buyer            the actor purchasing this item
     * @param map              the game map
     * @param terminalLocation the location of the shop terminal
     * @param wallet           the buyer's wallet
     * @return a confirmation message
     */
    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        return buyer + " purchased the Quake Charge";
    }

    /**
     * Triggers the shockwave at {@code epicenter}, degrading all
     * {@link game.grounds.CrackableGround} within the shockwave radius and pushing
     * items outward from the blast point.
     *
     * @param epicenter the location where this grenade detonates
     * @param map       the game map
     */
    @Override
    public void triggerShockwave(Location epicenter, GameMap map) {
        List<Location> affected = new ArrayList<>(epicenter.getNearbyLocations(getShockwaveRadius()));
        affected.add(epicenter);
        for (Location location : affected) {
            if (!location.getItems().isEmpty()) {
                List<Item> items = new ArrayList<>(location.getItems());
                for (Item item : items) {
                    Location target = findOneTilePushTarget(location, epicenter, map);
                    if (target != null) {
                        location.removeItem(item);
                        target.addItem(item);
                    }
                }
            }
            CrackableGround ground = location.getGroundAs(CrackableGround.class);
            if (ground != null) {
                location.setGround(ground.degrade());
            }
        }
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actionList = new ActionList();
        Location here = map.locationOf(owner);
        Location throwNorth = findThrowDestination(map, here, 0, -1);
        Location throwSouth = findThrowDestination(map, here, 0, 1);
        Location throwEast = findThrowDestination(map, here, 1, 0);
        Location throwWest = findThrowDestination(map, here, -1, 0);
        actionList.add(new GrenadeAction(this, "north", throwNorth));
        actionList.add(new GrenadeAction(this, "south", throwSouth));
        actionList.add(new GrenadeAction(this, "east", throwEast));
        actionList.add(new GrenadeAction(this, "west", throwWest));
        return actionList;
    }

    private Location findThrowDestination(GameMap map, Location origin, int dx, int dy) {
        Location lastValid = origin;
        for (int step = 1; step <= THROW_RANGE; step++) {
            Location candidate = locationAtOrNull(map, origin.x() + dx * step, origin.y() + dy * step);
            if (candidate == null || candidate.getGround().blocksThrownObjects()) {
                return lastValid;
            }
            lastValid = candidate;
        }
        return lastValid;
    }

    private Location locationAtOrNull(GameMap map, int x, int y) {
        try {
            return map.at(x, y);
        } catch (IndexOutOfBoundsException exception) {
            return null;
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                " (Remaining Uses: " +
                getStatistic(ItemStatistics.DURABILITY) + ")";
    }
}
