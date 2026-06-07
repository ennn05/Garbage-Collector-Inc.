package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;
import game.interfaces.Depositable;

import java.util.Random;

/**
 * Aluminium scrap dropped from cutting Aluminium Doors.
 * Can be deposited at the supercomputer for 50 company credits.
 * Has a 20% chance to damage the worker when depositing.
 */
public class AluminiumScrap extends Item implements Depositable {
    private static final int WEIGHT = 2;
    private static final int DEPOSIT_REWARD = 50;
    private static final double DAMAGE_CHANCE = 0.2;
    private static final int DAMAGE_ON_DEPOSIT = 5;

    private final Random random = new Random();

    /**
     * Constructor for AluminiumScrap.
     */
    public AluminiumScrap() {
        super("Aluminium Scrap", '%');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
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
     * Handle the deposit action with chance of taking damage.
     *
     * @param actor the actor performing the deposit
     * @param location the location of the supercomputer
     * @return result description of the deposit
     */
    @Override
    public String onDeposit(Actor actor, Location location) {
        String message = actor + " deposits Aluminium Scrap for " + DEPOSIT_REWARD + " company credits.";

        if (random.nextDouble() < DAMAGE_CHANCE) {
            actor.hurt(DAMAGE_ON_DEPOSIT);
            message += " OUCH! The jagged metal cuts " + actor + " for " + DAMAGE_ON_DEPOSIT + " damage!";
        }

        System.out.println(message);
        return message;
    }

    /**
     * Get the company credits reward for depositing.
     *
     * @return deposit reward (50 company credits)
     */
    @Override
    public int getDepositReward() {
        return DEPOSIT_REWARD;
    }
}
