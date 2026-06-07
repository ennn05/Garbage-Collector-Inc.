package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;
import game.world.FacilityAlarmSystem;

/**
 * A behaviour that moves a hostile entity directly toward the nearest worker
 * while the facility alarm is active.
 */
public class PursueWorkerBehaviour implements Behaviour<Actor, Action> {

    @Override
    public Action operate(Actor actor, Location location) {
        FacilityAlarmSystem alarmSystem = FacilityAlarmSystem.forMap(location.map());

        if (alarmSystem == null || !alarmSystem.isAlarmActive()) {
            return null;
        }

        Location targetLocation = findNearestWorker(location.map(), location);

        if (targetLocation == null) {
            return null;
        }

        Exit bestExit = null;
        int bestDistance = distance(location, targetLocation);

        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (!destination.canActorEnter(actor)) {
                continue;
            }

            int newDistance = distance(destination, targetLocation);

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

    /**
     * Find the nearest worker on the current map.
     *
     * @param map the current game map
     * @param currentLocation the actor's current location
     * @return the location of the nearest worker, or null if none exists
     */
    private Location findNearestWorker(GameMap map, Location currentLocation) {
        Location nearestWorker = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location candidate = map.at(x, y);

                if (candidate.containsAnActor() && candidate.getActor().hasAbility(Ability.WORKER)) { // only find worker
                    int currentDistance = distance(currentLocation, candidate);

                    if (currentDistance < shortestDistance) {
                        shortestDistance = currentDistance;
                        nearestWorker = candidate;
                    }
                }
            }
        }

        return nearestWorker;
    }

    /**
     * Calculate grid distance using Chebyshev distance,
     * which matches 8-direction movement.
     *
     * @param a first location
     * @param b second location
     * @return the distance between the two locations
     */
    private int distance(Location a, Location b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }
}