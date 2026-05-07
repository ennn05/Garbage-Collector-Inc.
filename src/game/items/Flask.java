package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;
import game.interfaces.Sterilisable;

/**
 * Due to severe budget cuts, the flask is only permitted to hold five (5)
 * mouthfuls of liquid per deployment. Employees are reminded not to consume
 * all five charges in a panic during a single encounter.
 */
public class Flask extends Item implements Sterilisable {
    private boolean sterilised = false;
    int totalUsable = 5;

    public Flask() {
        super("Flask", 'u');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(3));
    }

    /**
     * Check whether the flask still has remaining uses.
     *
     * @return true if the flask can still be consumed, false otherwise
     */
    public boolean hasUsesRemaining() {
        return totalUsable > 0;
    }

    /**
     * Get the remaining number of uses in the flask.
     *
     * @return remaining uses
     */
    public int getRemainingUses() {
        return totalUsable;
    }

    /**
     * Consume one use of the flask and heal the actor by 1 point,
     * if the flask still has remaining uses.
     *
     * @param actor the actor using the flask
     */
    public void consume(Actor actor) {
        if (hasUsesRemaining()) {
            totalUsable--;
            actor.heal(1);
        }
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