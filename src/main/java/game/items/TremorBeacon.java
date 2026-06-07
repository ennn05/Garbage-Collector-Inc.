package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.grounds.CrackableGround;
import game.interfaces.Purchasable;
import game.interfaces.Resonator;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeable beacon that triggers a shockwave when detonated by a {@link game.items.BeaconDetonator}.
 * <p>
 * The TremorBeacon is placed on the map (not thrown) and remains until a {@link game.actions.DetonateAction}
 * fires. On detonation it degrades all {@link game.grounds.CrackableGround} tiles within a radius of
 * {@value #SHOCKWAVE_RADIUS} and pushes items outward from its position.
 * Purchase price: {@value #PURCHASE_PRICE} credits. Weight: {@value #WEIGHT}.
 */
public class TremorBeacon extends Item implements Purchasable, Resonator {
    private static final int PURCHASE_PRICE = 10;
    private static final int SHOCKWAVE_POWER = 1;
    private static final int SHOCKWAVE_RADIUS = 2;
    private static final int WEIGHT = 10;

    /**
     * Creates a new TremorBeacon item (display character: {@code I}).
     */
    public TremorBeacon() {
        super("Tremor Beacon", 'I');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * {@inheritDoc}
     *
     * @return a new TremorBeacon instance
     */
    @Override
    public TremorBeacon createPurchasedItem() {
        return new TremorBeacon();
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
        String message = buyer + " cannot afford the Tremor Beacon (costs " + PURCHASE_PRICE + " credits).";
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
        return buyer + " purchased the Tremor Beacon";
    }

    /**
     * Triggers the beacon's shockwave at {@code epicenter}.
     * Degrades all {@link game.grounds.CrackableGround} within the shockwave radius
     * and pushes any items present on affected tiles outward from the epicenter.
     *
     * @param epicenter the detonation location of this beacon
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
}
