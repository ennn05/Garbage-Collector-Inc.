package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Sterilisable;

/**
 * Action for sterilising a sterilisable target.
 */
public class SteriliseAction extends Action {
    private final Sterilisable target;

    public SteriliseAction(Sterilisable target) {
        this.target = target;
    }

    /**
     * Sterilise the target if it has not already been sterilised.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return the result description
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (target.isSterilised()) {
            return target + " is already sterilised.";
        }

        target.sterilise();
        return actor + " sterilises " + target + ".";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " sterilises " + target;
    }
}