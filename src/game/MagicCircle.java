package game;

import game.actions.Teleportable;
import game.actions.TeleportAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A magic circle that allows within-map teleportation to other magic circles.
 * Using it randomly selects another magic circle on the map as the destination.
 * Spawns a Flask on an adjacent empty tile at the destination.
 */
public class MagicCircle extends Ground implements Teleportable {
    private Random random = new Random();

    /**
     * Constructor for MagicCircle.
     */
    public MagicCircle() {
        super('◎', "Magic Circle");
    }

    /**
     * Get all other magic circles on the map (excluding this one).
     * @param map the current game map
     * @return list of other magic circles on the map
     */
    @Override
    public List<Location> getDestinations(GameMap map) {
        List<Location> circles = new ArrayList<>();
        
        // Find all magic circle locations on the map
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Location loc = map.at(x, y);
                if (loc != null && loc.getGround() instanceof MagicCircle) {
                    circles.add(loc);
                }
            }
        }
        
        // Remove this circle if it's in the list
        Location thisLocation = null;
        // We need to find which location has this ground object
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Location loc = map.at(x, y);
                if (loc != null && loc.getGround() == this) {
                    thisLocation = loc;
                    break;
                }
            }
            if (thisLocation != null) break;
        }
        
        if (thisLocation != null) {
            circles.remove(thisLocation);
        }
        
        return circles;
    }

    /**
     * When teleported via MagicCircle, randomly pick one destination from the list
     * (actor has no choice) and spawn a Flask on an adjacent empty tile.
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the destination location (first in the list if multiple)
     * @param map the current game map
     */
    @Override
    public void onTeleport(Actor actor, Location source, Location destination, GameMap map) {
        // Get all possible destinations and randomly pick one
        List<Location> allCircles = getDestinations(map);
        if (!allCircles.isEmpty()) {
            Location randomDestination = allCircles.get(random.nextInt(allCircles.size()));
            
            // Move actor to the randomly selected circle (not the initially passed destination)
            map.moveActor(actor, randomDestination);
            
            // Spawn a Flask on an adjacent empty tile
            spawnFlaskOnAdjacentTile(randomDestination, map);
        }
    }

    /**
     * Get actions available at this location.
     * @param actor the actor
     * @param location the location of this ground
     * @return action list containing TeleportAction
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location) {
        ActionList actions = new ActionList();
        actions.add(new TeleportAction(this));
        return actions;
    }

    /**
     * Actors can enter magic circles.
     * @param actor the actor
     * @return true
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Spawn a Flask on an adjacent empty tile.
     * @param location the location to search for an adjacent empty tile
     * @param map the current game map
     */
    private void spawnFlaskOnAdjacentTile(Location location, GameMap map) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            
            // Check if tile is empty (no actor and ground is traversable)
            if (!adjacent.containsAnActor() && adjacent.getGround() instanceof Floor) {
                Flask flask = new Flask();
                adjacent.addItem(flask);
                return; // Only spawn one flask
            }
        }
    }
}
