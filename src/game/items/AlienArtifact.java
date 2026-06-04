package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.interfaces.Depositable;
import game.interfaces.Sellable;
import game.status.PoisonStatus;

import java.util.Random;

/**
 * Alien artifact dropped from cutting alien cubes from inventory.
 * Can be sold for 200 worker credits (50% poison chance) or 
 * deposited for 100 company credits with teleportation.
 */
public class AlienArtifact extends Item implements Sellable, Depositable {
    private static final int WEIGHT = 1;
    private static final int SELL_PRICE = 200;
    private static final int DEPOSIT_REWARD = 100;
    private static final double POISON_CHANCE = 0.5;
    private static final int POISON_DURATION = 5;
    private static final int POISON_DAMAGE = 1;

    private final Random random = new Random();

    /**
     * Constructor for AlienArtifact.
     */
    public AlienArtifact() {
        super("Alien Artifact", '?');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Get the sell price for the alien artifact.
     *
     * @return sell price (200 worker credits)
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Handle selling the alien artifact.
     * 50% chance to poison the seller.
     *
     * @param seller the actor selling this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the seller's wallet
     * @return result description
     */
    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        String message = seller + " sells the Alien Artifact for " + SELL_PRICE + " worker credits.";

        if (random.nextDouble() < POISON_CHANCE) {
            seller.addStatus(new PoisonStatus(POISON_DURATION, POISON_DAMAGE));
            message += " Handling the unstable artifact poisons " + seller + "!";
        }

        System.out.println(message);
        return message;
    }

    /**
     * Check if the actor can deposit this item.
     *
     * @param actor the actor attempting to deposit
     * @return true (always can be deposited)
     */
    @Override
    public boolean canBeDeposited(Actor actor) {
        return true;
    }

    /**
     * Handle depositing the alien artifact.
     * Teleports the worker to a random location on the map.
     *
     * @param actor the actor performing the deposit
     * @param location the location of the supercomputer
     * @return result description of the deposit
     */
    @Override
    public String onDeposit(Actor actor, Location location) {
        String message = actor + " deposits the Alien Artifact for " + DEPOSIT_REWARD +
                " company credits.";

        Location randomLocation = findRandomLocation(actor, location.map());
        if (randomLocation != null) {
            location.map().moveActor(actor, randomLocation);
            message += " The worker is teleported back to work!";
        }

        System.out.println(message);
        return message;
    }

    private Location findRandomLocation(Actor actor, edu.monash.fit2099.engine.positions.GameMap map) {
        Location current = map.locationOf(actor);
        int xMin = map.getXRange().min();
        int xMax = map.getXRange().max();
        int yMin = map.getYRange().min();
        int yMax = map.getYRange().max();

        for (int attempts = 0; attempts < 100; attempts++) {
            int x = xMin + (int) (Math.random() * (xMax - xMin + 1));
            int y = yMin + (int) (Math.random() * (yMax - yMin + 1));
            Location candidate = map.at(x, y);
            if (candidate != null && candidate != current && candidate.canActorEnter(actor)) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Get the company credits reward for depositing.
     *
     * @return deposit reward (100 company credits)
     */
    @Override
    public int getDepositReward() {
        return DEPOSIT_REWARD;
    }
}
