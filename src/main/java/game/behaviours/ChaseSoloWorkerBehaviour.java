package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

/**
 * Moves an actor toward the nearest worker without requiring the facility alarm.
 */
public class ChaseSoloWorkerBehaviour implements Behaviour<Actor, Action> {

    @Override
    public Action operate(Actor actor, Location location) {
        Location target = findNearestWorker(location.map(), location);

        if (target == null) {
            return null;
        }

        Exit bestExit = null;
        int bestDistance = chebyshev(location, target);

        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (!destination.canActorEnter(actor)) {
                continue;
            }

            int newDistance = chebyshev(destination, target);
            if (newDistance < bestDistance) {
                bestDistance = newDistance;
                bestExit = exit;
            }
        }

        if (bestExit == null) {
            return null;
        }

        return bestExit.getDestination().getMoveAction(actor, bestExit.getName(), bestExit.getHotKey());
    }

    private Location findNearestWorker(GameMap map, Location current) {
        Location nearest = null;
        int shortest = Integer.MAX_VALUE;

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location candidate = map.at(x, y);
                if (candidate.containsAnActor() && candidate.getActor().hasAbility(Ability.WORKER)) {
                    int d = chebyshev(current, candidate);
                    if (d < shortest) {
                        shortest = d;
                        nearest = candidate;
                    }
                }
            }
        }

        return nearest;
    }

    private int chebyshev(Location a, Location b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }
}
