package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.interfaces.Sellable;
import game.utility.FireSpawner;

import java.util.Random;

/**
 * A massive and heavy piece of obsolete display hardware.
 */
public class CRTMonitor extends Item implements Sellable {
    private static final int WEIGHT = 30;
    private static final int SELL_PRICE = 25;
    private static final int HEAL_AMOUNT = 5;
    private static final int SHORT_CHANCE_PERCENT = 20;
    private static final int SHORT_DAMAGE = 2;

    private final Random random = new Random();
    private final FireSpawner fireSpawner = new FireSpawner();

    public CRTMonitor() {
        super("CRT Monitor", '◙');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Get the selling price of the CRT monitor.
     *
     * @return selling price in credits
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Apply the selling effect of the CRT monitor.
     * Selling it heals the seller, but it may short out the terminal.
     *
     * @param seller the actor selling this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the seller's wallet
     * @return result description
     */
    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        seller.heal(HEAL_AMOUNT);

        String result = seller + " feels relieved after offloading the CRT Monitor and heals "
                + HEAL_AMOUNT + " health points.";

        if (random.nextInt(100) < SHORT_CHANCE_PERCENT) {
            seller.hurt(SHORT_DAMAGE);
            fireSpawner.spawnAround(terminalLocation);

            result += "\nThe CRT Monitor shorts out the terminal, dealing "
                    + SHORT_DAMAGE + " damage to " + seller
                    + " and spawning fire on all surrounding tiles.";
        }

        return result;
    }
}