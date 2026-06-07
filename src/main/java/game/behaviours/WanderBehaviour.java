package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A behaviour that makes an actor move randomly to a reachable adjacent location.
 */
public class WanderBehaviour implements Behaviour<Actor, Action> {
    private final Random random = new Random();

    @Override
    public Action operate(Actor actor, Location location) {
        List<Action> moveActions = new ArrayList<>();

        for (Exit exit : location.getExits()) {
            Action moveAction = exit.getDestination().getMoveAction(actor, "around", exit.getHotKey());
            if (moveAction != null) {
                moveActions.add(moveAction);
            }
        }

        if (moveActions.isEmpty()) {
            return null;
        }

        return moveActions.get(random.nextInt(moveActions.size()));
    }
}