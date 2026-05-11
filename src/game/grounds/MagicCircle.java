package game.grounds;

import game.interfaces.Teleportable;
import game.actions.TeleportAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import game.items.Flask;
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
     * Get a single randomly-selected other magic circle on the map (excluding this one).
     * Per REQ2, the game randomly teleports the actor - they have no choice.
     * @param map the current game map
     * @return list with a single random magic circle, or empty list if none found
     */
    @Override
    public List<Location> getDestinations(GameMap map) {
        List<Location> circles = new ArrayList<>();
        
        // Find all magic circle locations on the map
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc != null && !loc.containsAnActor() && loc.getGround() instanceof MagicCircle) {
                    circles.add(loc);
                }
            }
        }
        
        // Remove this circle if it's in the list
        Location thisLocation = null;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
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
        
        // Return only 1 randomly-selected destination
        List<Location> result = new ArrayList<>();
        if (!circles.isEmpty()) {
            result.add(circles.get(random.nextInt(circles.size())));
        }
        return result;
    }

    /**
     * When teleported via MagicCircle, spawn a Flask on an adjacent empty tile.
     * Note: The destination is already randomly selected by getDestinations() and the actor
     * has already been moved by DestinationChoiceAction.execute().
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the destination location (already randomly selected and actor moved here)
     * @param map the current game map
     */
    @Override
    public void onTeleport(Actor actor, Location source, Location destination, GameMap map) {
        // Spawn a Flask on an adjacent empty tile at the destination
        spawnFlaskOnAdjacentTile(destination);
    }

    /**
     * Get actions available at this location.
     * @param actor the actor
     * @param location the location of this ground
     * @param direction the direction of this ground from the actor
     * @return action list containing TeleportAction
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        // Random within-map teleportation: choose 1 destination circle now and expose a single action.
        // If there are no other circles, no action is offered.
        List<Location> destinations = getDestinations(location.map());
        if (!destinations.isEmpty()) {
            Location destination = destinations.get(0);
            if (destination != null && destination.canActorEnter(actor)) {
                actions.add(new TeleportAction(this, destination));
            }
        }
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
     */
    private void spawnFlaskOnAdjacentTile(Location location) {
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
