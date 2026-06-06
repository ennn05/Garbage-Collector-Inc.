package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.DetonateAction;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.interfaces.Purchasable;

public class BeaconDetonator extends Item implements Purchasable {
    private static final int DETONATE_RANGE = 5;
    private static final int PURCHASE_PRICE = 15;
    private static final int WEIGHT = 1;

    public BeaconDetonator() {
        super("Remote Detonator", '[');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return new ActionList(new DetonateAction(DETONATE_RANGE));
    }

    @Override
    public BeaconDetonator createPurchasedItem() {
        return new BeaconDetonator();
    }

    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
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
        String message = buyer + " cannot afford the Beacon Detonator (costs " + PURCHASE_PRICE + " credits).";
        System.out.println(message);
        return message;
    }

    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        return buyer + " purchased the Beacon Detonator";
    }
}
