package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.enums.ItemStatistics;
import game.interfaces.Drinkable;
import game.interfaces.Sterilisable;

/**
 * Due to severe budget cuts, the flask is only permitted to hold five (5)
 * mouthfuls of liquid per deployment. Employees are reminded not to consume
 * all five charges in a panic during a single encounter.
 */
public class Flask extends Item implements Sterilisable, Drinkable {
    private static final int WEIGHT = 3;
    private static final int HEAL_AMOUNT = 1;
    private static final int TOTAL_USABLE = 5;

    private boolean sterilised = false;

    public Flask() {
        super("Flask", 'u');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.DURABILITY, new BaseStatistic(TOTAL_USABLE));
    }

    /**
     * Check whether the flask still has remaining uses.
     *
     * @return true if the flask can still be consumed, false otherwise
     */
    public boolean hasUsesRemaining() {
        return this.getRemainingUses() > 0;
    }

    /**
     * Get the remaining number of uses in the flask.
     *
     * @return remaining uses
     */
    public int getRemainingUses() {
        return this.getStatistic(ItemStatistics.DURABILITY);
    }

    /**
     * Check whether this flask can be drunk.
     *
     * @return true if the flask still has remaining uses
     */
    @Override
    public boolean canDrink() {
        return hasUsesRemaining();
    }

    /**
     * Drink from the flask and heal the actor.
     *
     * @param actor the actor drinking from this flask
     * @return result description
     */
    @Override
    public String drink(Actor actor) {
        if (!hasUsesRemaining()) {
            return actor + " tries to drink from an empty flask.";
        }

        this.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, 1);
        actor.heal(HEAL_AMOUNT);

        return actor + " drinks flask, which heals them by " + HEAL_AMOUNT
                + " point of health. Remaining uses: " + this.getRemainingUses();
    }

    /**
     * Consume one use of the flask and heal the actor by 1 point,
     * if the flask still has remaining uses.
     *
     * @param actor the actor using the flask
     */
    public void consume(Actor actor) {
        drink(actor);
    }

    @Override
    public boolean isSterilised() {
        return sterilised;
    }

    @Override
    public void sterilise() {
        this.sterilised = true;
    }
}