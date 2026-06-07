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

/**
 * A handheld remote detonator that triggers all {@link TremorBeacon} items within range.
 * <p>
 * When used, a {@link game.actions.DetonateAction} is offered, which locates every
 * TremorBeacon within {@value #DETONATE_RANGE} tiles of the holder and fires their
 * shockwaves simultaneously. The beacons are consumed on detonation.
 * Purchase price: {@value #PURCHASE_PRICE} credits. Weight: {@value #WEIGHT}.
 */
public class BeaconDetonator extends Item implements Purchasable {
    private static final int DETONATE_RANGE = 5;
    private static final int PURCHASE_PRICE = 15;
    private static final int WEIGHT = 1;

    /**
     * Creates a new BeaconDetonator item (display character: {@code [}).
     */
    public BeaconDetonator() {
        super("Remote Detonator", '[');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Provides the {@link game.actions.DetonateAction} to the holder, allowing them
     * to detonate all TremorBeacons within {@value #DETONATE_RANGE} tiles.
     *
     * @param owner the actor holding this item
     * @param map   the current game map
     * @return an action list containing the detonate action
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return new ActionList(new DetonateAction(DETONATE_RANGE));
    }

    /**
     * {@inheritDoc}
     *
     * @return a new BeaconDetonator instance
     */
    @Override
    public BeaconDetonator createPurchasedItem() {
        return new BeaconDetonator();
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
     * @param buyer the actor attempting to buy this item
     * @param map   the map where the transaction happens
     * @return a message explaining the buyer cannot afford the item
     */
    @Override
    public String onInsufficientCredits(Actor buyer, GameMap map) {
        String message = buyer + " cannot afford the Beacon Detonator (costs " + PURCHASE_PRICE + " credits).";
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
        return buyer + " purchased the Beacon Detonator";
    }
}
