package game.items;

import game.interfaces.Teleportable;
import game.actions.TeleportAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An alien cube item that allows teleportation to 3 random locations on the map.
 * Each use by an actor randomly selects one of 3 possible destinations.
 */
public class AlienCube extends Item implements Teleportable {
    private List<Location> cachedDestinations = new ArrayList<>();
    private Random random = new Random();

    /**
     * Constructor for AlienCube.
     */
    public AlienCube() {
        super("Alien Cube", 'Ã', true, 3);
    }

    /**
     * Get 3 randomly-selected locations from the map for teleportation.
     * These are the possible destinations the actor can choose from.
     * @param map the current game map
     * @return list of 3 random locations on the map that are Floor tiles
     */
    @Override
    public List<Location> getDestinations(GameMap map) {
        List<Location> validDestinations = new ArrayList<>();
        
        // Collect all valid destination tiles (Floor)
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Location loc = map.at(x, y);
                if (loc != null && loc.getGround() instanceof game.grounds.Floor) {
                    validDestinations.add(loc);
                }
            }
        }
        
        // Select up to 3 random destinations
        List<Location> selectedDestinations = new ArrayList<>();
        for (int i = 0; i < 3 && !validDestinations.isEmpty(); i++) {
            Location selected = validDestinations.get(random.nextInt(validDestinations.size()));
            selectedDestinations.add(selected);
            validDestinations.remove(selected); // Don't select the same location twice
        }
        
        return selectedDestinations;
    }

    /**
     * Handle teleportation side effects.
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the destination location
     * @param map the current game map
     */
    @Override
    public void onTeleport(Actor actor, Location source, Location destination, GameMap map) {
        // AlienCube has no special effects, just teleports the actor
        // The teleportation itself is handled by the teleport action
    }

    /**
     * Get actions available for the player using this item.
     * @param actor the actor (presumed to be the player holding the item)
     * @param location the location of the actor
     * @return action list containing TeleportAction
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location) {
        ActionList actions = new ActionList();
        actions.add(new TeleportAction(this));
        return actions;
    }

    @Override
    public String toString() {
        return "Alien Cube";
    }
}
