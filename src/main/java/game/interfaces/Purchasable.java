package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.economy.Wallet;

/**
 * Represents an item that can be purchased from the Supercomputer.
 */
public interface Purchasable {

    /**
     * Get the purchase price of this item.
     *
     * @return purchase price in credits
     */
    int getPurchasePrice();

    /**
     * Create a new item instance after a successful purchase.
     *
     * @return the purchased item
     */
    Item createPurchasedItem();

    /**
     * Apply the effect that happens immediately after this item is purchased.
     *
     * @param buyer the actor buying this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the buyer's wallet
     * @return result description
     */
    String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet);

    /**
     * Apply the effect when the actor attempts to buy this item without enough credits.
     *
     * @param buyer the actor attempting to buy this item
     * @param map the map where the transaction happens
     * @return result description
     */
    String onInsufficientCredits(Actor buyer, GameMap map);
}