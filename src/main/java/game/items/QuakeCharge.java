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

public class QuakeCharge extends Item implements Purchasable, Resonator {
    private static final int INITIAL_DURABILITY = 3;
    private static final int PURCHASE_PRICE = 55;
    private static final int SHOCKWAVE_POWER = 1;
    private static final int SHOCKWAVE_RADIUS = 1;
    private static final int THROW_RANGE = 3;
    private static final int WEIGHT = 3;

    public QuakeCharge() {
        super("Quake Grenade", '@');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.DURABILITY, new BaseStatistic(INITIAL_DURABILITY));
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    @Override
    public QuakeCharge createPurchasedItem() {
        return new QuakeCharge();
    }

    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    @Override
    public int getShockwavePower() {
        return SHOCKWAVE_POWER;
    }

    @Override
    public int getShockwaveRadius() {
        return SHOCKWAVE_RADIUS;
    }

    /**
     * Handle insufficient credits response.
     *
     * @param buyer the actor attempting to buy this item
     * @param map the map where the transaction happens
     * @return result description
     */
    @Override
    public String onInsufficientCredits(Actor buyer, GameMap map) {
        String message = buyer + " cannot afford the Quake Charge (costs " + PURCHASE_PRICE + " credits).";
        System.out.println(message);
        return message;
    }

    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        return buyer + " purchased the Quake Charge";
    }

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
        Location throwNorth = map.at(here.x(), here.y() - THROW_RANGE);
        Location throwSouth = map.at(here.x(), here.y() + THROW_RANGE);
        Location throwEast = map.at(here.x() + THROW_RANGE, here.y());
        Location throwWest = map.at(here.x() - THROW_RANGE, here.y());
        for (int i = 1; i <= THROW_RANGE; i++) {
            if (map.at(here.x(), here.y() - i).getGround().blocksThrownObjects()) {
                throwNorth = map.at(here.x(), here.y() - i + 1);
                break;
            }
        }
        for (int i = 1; i <= THROW_RANGE; i++) {
            if (map.at(here.x(), here.y() + i).getGround().blocksThrownObjects()) {
                throwSouth = map.at(here.x(), here.y() + i - 1);
                break;
            }
        }
        for (int i = 1; i <= THROW_RANGE; i++) {
            if (map.at(here.x() + i, here.y()).getGround().blocksThrownObjects()) {
                throwEast = map.at(here.x() + i - 1, here.y());
                break;
            }
        }
        for (int i = 1; i <= THROW_RANGE; i++) {
            if (map.at(here.x() - i, here.y()).getGround().blocksThrownObjects()) {
                throwWest = map.at(here.x() - i + 1, here.y());
                break;
            }
        }
        actionList.add(new GrenadeAction(this, "north", throwNorth));
        actionList.add(new GrenadeAction(this, "south", throwSouth));
        actionList.add(new GrenadeAction(this, "east", throwEast));
        actionList.add(new GrenadeAction(this, "west", throwWest));
        return actionList;
    }

    @Override
    public String toString() {
        return super.toString() +
                " (Remaining Uses: " +
                getStatistic(ItemStatistics.DURABILITY) + ")";
    }
}
