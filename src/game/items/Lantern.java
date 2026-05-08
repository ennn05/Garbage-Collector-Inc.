package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.grounds.Fire;
import game.interfaces.Sellable;
import game.status.BurnStatus;
import game.utility.FireSpawner;

import java.util.Random;

/**
 * An unstable lantern that may leak oil and ignite the ground while being carried.
 */
public class Lantern extends Item implements Sellable {
    private static final int WEIGHT = 7;
    private static final int INITIAL_FUEL = 10;
    private static final int LEAK_CHANCE_PERCENT = 5;

    private static final int SELL_PRICE_PER_OIL = 5;
    private static final int SALE_BURN_CHANCE_PERCENT = 50;
    private static final int SALE_FIRE_CHANCE_PERCENT = 25;
    private static final int SALE_BURN_TURNS = 3;
    private static final int SALE_BURN_DAMAGE = 2;

    private int oilFuel;
    private final Random random = new Random();
    private final FireSpawner fireSpawner = new FireSpawner();

    public Lantern() {
        super("Lantern", '&');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.oilFuel = INITIAL_FUEL;
    }

    /**
     * Get the remaining oil fuel.
     *
     * @return remaining oil fuel
     */
    public int getOilFuel() {
        return oilFuel;
    }

    /**
     * While being carried, the lantern has a chance to leak and ignite the ground.
     *
     * @param currentLocation the actor's current location
     * @param actor the actor carrying the lantern
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        if (oilFuel <= 0) {
            return;
        }

        if (currentLocation.getGround() instanceof Fire) {
            return;
        }

        if (random.nextInt(100) < LEAK_CHANCE_PERCENT) {
            oilFuel--;
            currentLocation.setGround(new Fire(currentLocation.getGround()));
        }
    }

    /**
     * Get the selling price of the lantern.
     * Each remaining oil unit is worth 5 credits.
     *
     * @return selling price in credits
     */
    @Override
    public int getSellPrice() {
        return oilFuel * SELL_PRICE_PER_OIL;
    }

    /**
     * Apply the selling effect of the lantern.
     * Burning and fire spawning are independent events.
     *
     * @param seller the actor selling this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the seller's wallet
     * @return result description
     */
    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        String result = "";

        if (random.nextInt(100) < SALE_BURN_CHANCE_PERCENT) {
            seller.addStatus(new BurnStatus(SALE_BURN_TURNS, SALE_BURN_DAMAGE));
            result += seller + " is burned for " + SALE_BURN_TURNS
                    + " turns, taking " + SALE_BURN_DAMAGE + " damage per turn.";
        }

        if (random.nextInt(100) < SALE_FIRE_CHANCE_PERCENT) {
            fireSpawner.spawnAround(terminalLocation);

            if (!result.isEmpty()) {
                result += "\n";
            }

            result += "The lantern ignites and spawns fire on all surrounding tiles.";
        }

        if (result.isEmpty()) {
            result = "The lantern is sold without causing any additional incident.";
        }

        return result;
    }
}