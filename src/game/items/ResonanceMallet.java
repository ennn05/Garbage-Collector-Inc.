package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.MalletAction;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.grounds.CrackableGround;
import game.interfaces.Purchasable;
import game.interfaces.Resonator;

import java.util.ArrayList;
import java.util.List;

public class ResonanceMallet extends Item implements Purchasable, Resonator {
    private static final int COOLDOWN = 10;
    private static final int PURCHASE_PRICE = 50;
    private static final int SHOCKWAVE_POWER = 1;
    private static final int SHOCKWAVE_RADIUS = 0;
    private static final int WEIGHT = 5;

    public ResonanceMallet() {
        super("Resonance Mallet", 'p');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.COOLDOWN, new BaseStatistic(COOLDOWN));
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        if (!isOnCooldown()) {
            ActionList actionList = new ActionList();
            Location here = map.locationOf(owner);
            Location north = map.at(here.x(), here.y() - 1);
            Location south = map.at(here.x(), here.y() + 1);
            Location west = map.at(here.x() - 1, here.y());
            Location east = map.at(here.x() + 1, here.y());
            actionList.add(new MalletAction(north, "North", this));
            actionList.add(new MalletAction(south, "South", this));
            actionList.add(new MalletAction(west, "West", this));
            actionList.add(new MalletAction(east, "East", this));
            return actionList;
        }
        return new ActionList();
    }

    public void applyCooldown() {
        this.modifyStatistic(ItemStatistics.COOLDOWN, StatisticOperations.UPDATE, 0);
    }

    @Override
    public ResonanceMallet createPurchasedItem() {
        return new ResonanceMallet();
    }

    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    @Override
    public int getShockwaveRadius() {
        return SHOCKWAVE_RADIUS;
    }

    @Override
    public int getShockwavePower() {
        return SHOCKWAVE_POWER;
    }

    public boolean isOnCooldown() {
        return this.getStatistic(ItemStatistics.COOLDOWN) < COOLDOWN;
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
        String message = buyer + " cannot afford the Resonance Mallet (costs " + PURCHASE_PRICE + " credits).";
        System.out.println(message);
        return message;
    }

    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        return buyer + " purchased the Resonance Mallet";
    }

    @Override
    public void tick(Location currentLocation, Actor actor) {
        if (isOnCooldown()) {
            this.modifyStatistic(ItemStatistics.COOLDOWN, StatisticOperations.INCREASE, 1);
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                " (Cooldown: " + this.getStatistic(ItemStatistics.COOLDOWN) + "/" + COOLDOWN + ")";
    }

    @Override
    public void triggerShockwave(Location epicenter, GameMap map) {
        if (!epicenter.getItems().isEmpty()) {
            List<Item> items = new ArrayList<>(epicenter.getItems());
            for (Item item : items) {
                Location target = findOneTilePushTarget(epicenter, epicenter, map);
                if (target != null) {
                    epicenter.removeItem(item);
                    target.addItem(item);
                }
            }
        }
        CrackableGround ground = epicenter.getGroundAs(CrackableGround.class);
        if (ground != null) ground.degrade();
    }
}
