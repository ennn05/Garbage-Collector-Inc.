package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.interfaces.Purchasable;
import game.status.BurnStatus;

/**
 * A plasma cutter tool that can cut through doors, vents, and alien cubes.
 * When purchased, the worker takes 5 damage and is burned for 5 turns.
 */
public class PlasmaCutter extends Item implements Purchasable {
    private static final int WEIGHT = 7;
    private static final int PURCHASE_PRICE = 50;
    private static final int BURN_DAMAGE_ON_PURCHASE = 5;
    private static final int BURN_TURNS = 5;
    private static final int BURN_DAMAGE_PER_TURN = 1;

    /**
     * Constructor for PlasmaCutter.
     */
    public PlasmaCutter() {
        super("Plasma Cutter", '>');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Get the purchase price of the plasma cutter.
     *
     * @return purchase price (50 worker credits)
     */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * Create a new instance of the plasma cutter.
     *
     * @return a new PlasmaCutter item
     */
    @Override
    public Item createPurchasedItem() {
        return new PlasmaCutter();
    }

    /**
     * Apply purchase effects: damage and burn status.
     *
     * @param buyer the actor buying this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the buyer's wallet
     * @return result description
     */
    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        buyer.hurt(BURN_DAMAGE_ON_PURCHASE);
        buyer.addStatus(new BurnStatus(BURN_TURNS, BURN_DAMAGE_PER_TURN));
        
        String message = buyer + " receives the Plasma Cutter! It's ejected from the delivery chute " +
                "at searing temperatures, dealing " + BURN_DAMAGE_ON_PURCHASE + " damage and burning for " +
                BURN_TURNS + " turns!";
        System.out.println(message);
        
        return message;
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
        String message = buyer + " cannot afford the Plasma Cutter (costs " + PURCHASE_PRICE + " credits).";
        System.out.println(message);
        return message;
    }
}
