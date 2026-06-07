package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Consumable;

/**
 * Action for consuming a consumable target.
 */
public class ConsumeAction extends Action {
    private final Consumable target;

    public ConsumeAction(Consumable target) {
        this.target = target;
    }

    /**
     * Consume the target if it can currently be consumed.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return result description
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!target.canConsume(actor)) {
            return actor + " cannot consume " + target + " right now.";
        }

        return target.consume(actor);
    }

    @Override
    public String menuDescription(Actor actor) {
        return target.menuDescription(actor);
    }
}