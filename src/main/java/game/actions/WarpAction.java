package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Teleports an actor to a randomly selected unoccupied, traversable location on the current map.
 *
 * <p>This action is forced (not player-chosen) and is invoked directly by
 * {@link game.flora.WarperMatureTree} when a worker enters an adjacent tile.
 * It is intentionally separate from {@link TeleportAction}, which handles
 * player-initiated teleportation to specific destinations via a {@link game.interfaces.Teleportable} device.
 *
 * @author ennn12
 */
public class WarpAction extends Action {

    private static final Random random = new Random();

    /**
     * Collects all valid destinations on the map, picks one at random, and moves the actor there.
     *
     * @param actor the actor being warped
     * @param map   the current game map
     * @return a string describing the outcome
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        List<Location> validLocations = new ArrayList<>();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.canActorEnter(actor)) {
                    validLocations.add(loc);
                }
            }
        }
        if (validLocations.isEmpty()) {
            return actor + " resisted the warp!";
        }
        Location destination = validLocations.get(random.nextInt(validLocations.size()));
        map.moveActor(actor, destination);
        return actor + " was grabbed by warper tree roots and warped to a random location!";
    }

    /**
     * Returns a menu description (not normally shown, since this action is forced).
     *
     * @param actor the actor being warped
     * @return menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " is warped to a random location";
    }
}
