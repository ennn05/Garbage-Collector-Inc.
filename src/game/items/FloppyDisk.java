package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.interfaces.Sellable;

import java.util.Random;

/**
 * A lightweight piece of ancient storage technology.
 */
public class FloppyDisk extends Item implements Sellable {
    private static final int WEIGHT = 1;
    private static final int SELL_PRICE = 1;
    private static final int GLITCH_CHANCE_PERCENT = 50;
    private static final int GLITCH_DEDUCTION = 50;

    private final Random random = new Random();

    public FloppyDisk() {
        super("Floppy Disk", '⊟');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Get the selling price of the floppy disk.
     *
     * @return selling price in credits
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Apply the selling effect of the floppy disk.
     * The Supercomputer may glitch and deduct credits after the sale.
     *
     * @param seller the actor selling this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the seller's wallet
     * @return result description
     */
    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        if (random.nextInt(100) < GLITCH_CHANCE_PERCENT) {
            int deductedCredits = wallet.deductCredits(GLITCH_DEDUCTION);

            return "The Supercomputer glitches and attempts to deduct "
                    + GLITCH_DEDUCTION + " credits from " + seller + "."
                    + "\nActual credits deducted: " + deductedCredits + ".";
        }

        return "The floppy disk is sold without triggering a Supercomputer glitch.";
    }
}