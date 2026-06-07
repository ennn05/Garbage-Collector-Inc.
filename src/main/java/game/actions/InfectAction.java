package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Infectable;

/**
 * An action that infects a target and causes the acting actor to become unconscious.
 */
public class InfectAction extends Action {
    private final Infectable target;
    private final String direction;

    /**
     * Creates a new infect action for the specified target and direction.
     *
     * @param target the infectable target to be infected
     * @param direction the direction of the target relative to the actor
     */
    public InfectAction(Infectable target, String direction) {
        this.target = target;
        this.direction = direction;
    }

    /**
     * Executes the infection action.
     * <p>
     * The acting actor becomes unconscious, and then the target is infected.
     *
     * @param actor the actor performing the action
     * @param gameMap the map the action is being performed on
     * @return a message describing the result of the action
     */
    @Override
    public String execute(Actor actor, GameMap gameMap) {
        return actor.unconscious(gameMap) + "\n" + target.infect(actor, gameMap);
    }

    /**
     * Describes this action in menu form.
     *
     * @param actor the actor who may perform the action
     * @return the menu description for this action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " infects " + target + " at " + direction;
    }
}
