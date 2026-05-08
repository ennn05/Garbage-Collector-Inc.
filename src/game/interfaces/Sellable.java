package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.economy.Wallet;

/**
 * Represents an item that can be sold to the Supercomputer.
 */
public interface Sellable {

    /**
     * Get the current selling price of this item.
     * This supports both fixed and dynamic prices.
     *
     * @return selling price in credits
     */
    int getSellPrice();

    /**
     * Apply the effect that happens immediately after this item is sold.
     *
     * @param seller the actor selling this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the seller's wallet
     * @return result description
     */
    String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet);
}