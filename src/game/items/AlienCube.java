package game.items;

import game.interfaces.Teleportable;
import game.actions.TeleportAction;
import game.grounds.ToxicWaste;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.actions.ActionList;
import game.actors.Undead;
import game.economy.Wallet;
import game.grounds.Floor;
import game.interfaces.Sellable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An alien cube item that allows teleportation to 3 random locations on the map.
 * Each use by an actor randomly selects one of 3 possible destinations.
 */
public class AlienCube extends Item implements Teleportable, Sellable {
    private static final int SELL_PRICE = 25;
    private final Random random = new Random();

    /**
     * Constructor for AlienCube.
     */
    public AlienCube() {
        super("Alien Cube", 'Ã');
        this.makePortable();
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
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc != null && !loc.containsAnActor() && loc.getGround() instanceof game.grounds.Floor) {
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
     * Ripping a hole in reality corrupts all adjacent ground tiles to Toxic Waste.
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the destination location
     * @param map the current game map
     */
    @Override
    public void onTeleport(Actor actor, Location source, Location destination, GameMap map) {
        // Corrupt all adjacent tiles at source location to Toxic Waste
        corruptAdjacentTiles(source);
    }

    /**
     * Turn all adjacent floor tiles to Toxic Waste.
     * @param location the source location whose adjacent tiles to corrupt
     */
    private void corruptAdjacentTiles(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            // Only corrupt floor-like tiles (not walls, etc.)
            if (adjacent.getGround() instanceof game.grounds.Floor) {
                adjacent.setGround(new ToxicWaste());
            }
        }
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        for (Location destination : getDestinations(map)) {
            if (destination != null && destination.canActorEnter(owner)) {
                actions.add(new TeleportAction(this, destination));
            }
        }
        return actions;
    }

    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        Location sellerLocation = map.locationOf(seller);
        for (Exit exit : sellerLocation.getExits()) {
            Location candidate = exit.getDestination();
            if (!candidate.containsAnActor() && candidate.getGround() instanceof Floor) {
                try {
                    map.addActor(new Undead(), candidate);
                    return "Reality shudders. An Undead emerges beside " + seller + ".";
                } catch (Exception ignored) {
                    break;
                }
            }
        }
        return "Reality warps briefly, but nothing manifests nearby.";
    }

    @Override
    public String toString() {
        return "Alien Cube";
    }
}
