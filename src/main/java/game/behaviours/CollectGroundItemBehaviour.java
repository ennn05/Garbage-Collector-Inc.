package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.PickUpItemFromGroundAction;

/**
 * Makes an actor seek out and collect items from the ground.
 * Checks the current tile first, then adjacent tiles, then searches the full map
 * for the nearest item and moves toward it.
 */
public class CollectGroundItemBehaviour implements Behaviour<Actor, Action> {
    private static final int MAX_INVENTORY_SIZE = 3;

    @Override
    public Action operate(Actor actor, Location location) {
        if (actor.getInventory().getItems().size() >= MAX_INVENTORY_SIZE) {
            return null;
        }

        if (!location.getItems().isEmpty()) {
            return new PickUpItemFromGroundAction(location.getItems().get(0));
        }

        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (!dest.getItems().isEmpty() && dest.canActorEnter(actor)) {
                return new PickUpItemFromGroundAction(dest, exit.getName(), dest.getItems().get(0));
            }
        }

        Location itemTarget = findNearestItemLocation(location.map(), location, actor);
        if (itemTarget == null) {
            return null;
        }

        Exit bestExit = null;
        int bestDistance = chebyshev(location, itemTarget);

        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (!dest.canActorEnter(actor)) {
                continue;
            }
            int d = chebyshev(dest, itemTarget);
            if (d < bestDistance) {
                bestDistance = d;
                bestExit = exit;
            }
        }

        if (bestExit == null) {
            return null;
        }

        return bestExit.getDestination().getMoveAction(actor, bestExit.getName(), bestExit.getHotKey());
    }

    private Location findNearestItemLocation(GameMap map, Location current, Actor actor) {
        Location nearest = null;
        int shortest = Integer.MAX_VALUE;

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location candidate = map.at(x, y);
                if (!candidate.getItems().isEmpty() && !candidate.containsAnActor()) {
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
