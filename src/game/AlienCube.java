package game;

import game.actions.Teleportable;
import game.actions.TeleportAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An alien cube that can be used as a teleportation device or sold for credits.
 * When used, presents 3 random locations within the current map as destinations.
 * Using it corrupts the source location by turning adjacent tiles into ToxicWaste.
 * Can be sold to the Supercomputer for 25 credits, spawning an Undead nearby.
 */
public class AlienCube extends Item implements Teleportable {
    private static final int SELL_VALUE = 25;
    private Random random = new Random();

    /**
     * Constructor for AlienCube.
     */
    public AlienCube() {
        super("Alien Cube", '◈');
        addAbility(ItemAbility.PORTABLE);
    }

    /**
     * Get 3 random locations from the current map as destinations.
     * @param map the current game map
     * @return list of 3 random destination locations
     */
    @Override
    public List<Location> getDestinations(GameMap map) {
        List<Location> allLocations = getAllLocationInMap(map);
        List<Location> selectedDestinations = new ArrayList<>();

        // Randomly select up to 3 locations
        for (int i = 0; i < 3 && !allLocations.isEmpty(); i++) {
            int randomIndex = random.nextInt(allLocations.size());
            selectedDestinations.add(allLocations.remove(randomIndex));
        }

        return selectedDestinations;
    }

    /**
     * When teleported via AlienCube, corrupt the source location by setting
     * all adjacent ground tiles to ToxicWaste.
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the destination location
     * @param map the current game map
     */
    @Override
    public void onTeleport(Actor actor, Location source, Location destination, GameMap map) {
        // Convert all adjacent tiles to ToxicWaste
        for (Exit exit : source.getExits()) {
            Location adjacent = exit.getDestination();
            Ground ground = adjacent.getGround();
            // Only convert ground tiles (not walls, doors, etc.)
            if (ground instanceof Floor || ground instanceof Dirt) {
                adjacent.setGround(new ToxicWaste());
            }
        }
    }

    /**
     * Get actions available when this item is in the actor's inventory.
     * @param owner the actor carrying this item
     * @param map the current game map
     * @return a list containing TeleportAction
     */
    @Override
    public List<Action> allowableActions(Actor owner, GameMap map) {
        List<Action> actions = new ArrayList<>();
        actions.add(new TeleportAction(this));
        return actions;
    }

    /**
     * Get the sell value of this cube.
     * @return 25 credits
     */
    public int getSellValue() {
        return SELL_VALUE;
    }

    /**
     * Get all locations in a map for random destination selection.
     * @param map the game map
     * @return list of all traversable locations in the map
     */
    private List<Location> getAllLocationInMap(GameMap map) {
        List<Location> allLocations = new ArrayList<>();
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Location loc = map.at(x, y);
                if (loc != null && loc.getGround() instanceof Floor) {
                    allLocations.add(loc);
                }
            }
        }
        return allLocations;
    }
}
