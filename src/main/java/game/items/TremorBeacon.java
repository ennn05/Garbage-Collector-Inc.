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

public class TremorBeacon extends Item implements Purchasable, Resonator {
    private static final int PURCHASE_PRICE = 10;
    private static final int SHOCKWAVE_POWER = 1;
    private static final int SHOCKWAVE_RADIUS = 2;
    private static final int WEIGHT = 10;

    public TremorBeacon() {
        super("Tremor Beacon", 'I');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    @Override
    public TremorBeacon createPurchasedItem() {
        return new TremorBeacon();
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
        String message = buyer + " cannot afford the Tremor Beacon (costs " + PURCHASE_PRICE + " credits).";
        System.out.println(message);
        return message;
    }

    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        return buyer + " purchased the Tremor Beacon";
    }

    @Override
    public void triggerShockwave(Location epicenter, GameMap map) {
        List<Location> affected = epicenter.getNearbyLocations(getShockwaveRadius());
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
            if (ground != null) ground.degrade();
        }
    }
}
