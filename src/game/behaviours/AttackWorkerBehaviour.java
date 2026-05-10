package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackWorkerAction;
import game.enums.Ability;

/**
 * A behaviour that makes an actor attack one adjacent worker if possible.
 */
public class AttackWorkerBehaviour implements Behaviour<Actor, Action> {

    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (destination.containsAnActor()) {
                Actor target = destination.getActor();

                if (target.hasAbility(Ability.WORKER)) {
                    return new AttackWorkerAction(target, exit.getName());
                }
            }
        }

        return null;
    }
}