package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.DecoyAction;
import game.enums.Ability;

/**
 * While disguised, the Mannequin heals itself each turn when adjacent to a worker.
 * Nearby workers unknowingly reinforce the Mannequin by allowing it to regenerate HP.
 */
public class DecoyBehaviour implements Behaviour<Actor, Action> {
    private static final int HEAL_AMOUNT = 2;

    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (!dest.containsAnActor()) {
                continue;
            }
            Actor worker = dest.getActor();
            if (!worker.hasAbility(Ability.WORKER)) {
                continue;
            }

            return new DecoyAction(worker, HEAL_AMOUNT);
        }

        return null;
    }
}
