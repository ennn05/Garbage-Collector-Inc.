package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.InfectAction;
import game.interfaces.Infectable;

/**
 * A behaviour that allows an actor to infect one adjacent Infectable target if possible.
 */
public class InfectBehaviour implements Behaviour<Actor, Action> {

    /**
     * Produces an action to infect an adjacent Infectable target, or null if there are no valid targets.
     * Adjacent actors are checked first. If there is no infectable actor, this behaviour then checks
     * items on the adjacent ground, such as Lanterns and Cookies.
     *
     * @param actor the entity performing the behaviour
     * @param location the location of the current entity
     * @return an infecting action on an adjacent Infectable target, or null if there are no valid targets
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (destination.containsAnActor()) {
                Infectable target = destination.getActorAs(Infectable.class);

                if (target != null) {
                    return new InfectAction(target, exit.getName());
                }
            }

            for (Item item : destination.getItems()) {
                Infectable target = item.asCapability(Infectable.class).orElse(null);

                if (target != null) {
                    return new InfectAction(target, exit.getName());
                }
            }
        }

        return null;
    }
}