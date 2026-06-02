package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.TeleportAction;
import game.actors.Undead;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.grounds.ToxicWaste;
import game.interfaces.Cuttable;
import game.interfaces.Sellable;
import game.interfaces.Teleportable;
import game.status.PoisonStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An alien cube item that allows teleportation to random locations on the map.
 * Each use by an actor presents up to 3 possible destinations.
 * Can be cut with a plasma cutter while in inventory.
 */
public class AlienCube extends Item implements Teleportable, Sellable, Cuttable {
    private static final int SELL_PRICE = 25;
    private static final int DESTINATION_COUNT = 3;
    private static final int POISON_DURATION = 5;
    private static final int POISON_DAMAGE = 1;

    private final Random random = new Random();

    /**
     * Constructor for AlienCube.
     */
    public AlienCube() {
        super("Alien Cube", '◈');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(5));
    }

    /**
     * Get possible destination locations for teleportation.
     * This interface method does not know which actor is teleporting, so it returns
     * unoccupied locations. The actor-specific enterability check is applied in allowableActions().
     *
     * @param map the current game map
     * @return list of unoccupied locations on the map
     */
    @Override
    public List<Location> getDestinations(GameMap map) {
        List<Location> validDestinations = new ArrayList<>();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location location = map.at(x, y);

                if (location != null && !location.containsAnActor()) {
                    validDestinations.add(location);
                }
            }
        }

        return selectRandomDestinations(validDestinations);
    }

    /**
     * Get up to 3 random locations that the given actor can enter.
     *
     * @param map the current game map
     * @param actor the actor using the Alien Cube
     * @return list of selected destination locations
     */
    private List<Location> getDestinationsForActor(GameMap map, Actor actor) {
        List<Location> validDestinations = new ArrayList<>();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location location = map.at(x, y);

                if (location != null
                        && !location.containsAnActor()
                        && location.canActorEnter(actor)) {
                    validDestinations.add(location);
                }
            }
        }

        return selectRandomDestinations(validDestinations);
    }

    /**
     * Select up to three random destination locations.
     *
     * @param validDestinations all valid destination candidates
     * @return randomly selected destinations
     */
    private List<Location> selectRandomDestinations(List<Location> validDestinations) {
        List<Location> selectedDestinations = new ArrayList<>();

        for (int i = 0; i < DESTINATION_COUNT && !validDestinations.isEmpty(); i++) {
            Location selected = validDestinations.get(random.nextInt(validDestinations.size()));
            selectedDestinations.add(selected);
            validDestinations.remove(selected);
        }

        return selectedDestinations;
    }

    /**
     * Handle teleportation side effects.
     * Ripping a hole in reality corrupts adjacent enterable tiles to Toxic Waste.
     *
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the destination location
     * @param map the current game map
     */
    @Override
    public void onTeleport(Actor actor, Location source, Location destination, GameMap map) {
        corruptAdjacentTiles(source, actor);
    }

    /**
     * Turn adjacent enterable tiles into Toxic Waste.
     *
     * @param location the source location whose adjacent tiles are corrupted
     * @param actor the actor using the Alien Cube
     */
    private void corruptAdjacentTiles(Location location, Actor actor) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();

            if (!adjacent.containsAnActor() && adjacent.canActorEnter(actor)) {
                adjacent.setGround(new ToxicWaste());
            }
        }
    }

    /**
     * Get teleport actions available to the owner.
     *
     * @param owner the actor carrying the Alien Cube
     * @param map the map the actor is on
     * @return available teleport actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        for (Location destination : getDestinationsForActor(map, owner)) {
            actions.add(new TeleportAction(this, destination));
        }

        return actions;
    }

    /**
     * Get the selling price.
     *
     * @return selling price in credits
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Apply Alien Cube sale effect.
     * When sold, the cube attempts to spawn one Undead beside the seller.
     *
     * @param seller the actor selling this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the seller's wallet
     * @return result description
     */
    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        Location sellerLocation = map.locationOf(seller);

        for (Exit exit : sellerLocation.getExits()) {
            Location candidate = exit.getDestination();
            Undead undead = new Undead();

            if (!candidate.containsAnActor() && candidate.canActorEnter(undead)) {
                try {
                    map.addActor(undead, candidate);
                    return "Reality shudders. An Undead emerges beside " + seller + ".";
                } catch (Exception ignored) {
                    break;
                }
            }
        }

        return "Reality warps briefly, but nothing manifests nearby.";
    }

    /**
     * Check if this alien cube can be cut.
     * Can only be cut if it's in an actor's inventory.
     *
     * @param actor the actor attempting to cut
     * @return true if the cube is in the actor's inventory
     */
    @Override
    public boolean canBeCut(Actor actor) {
        return actor.getInventory().getItems().contains(this);
    }

    /**
     * Handle cutting the alien cube.
     * Inflicts poison status on the actor.
     *
     * @param actor the actor performing the cut
     * @param location the location of the actor (not used for inventory items)
     * @return result description of the cutting
     */
    @Override
    public String onCut(Actor actor, Location location) {
        actor.addStatus(new PoisonStatus(POISON_DURATION, POISON_DAMAGE));
        String result = actor + " cuts open the Alien Cube with the Plasma Cutter! " +
                "It releases a toxic substance, poisoning " + actor + " for " + 
                POISON_DURATION + " turns!";
        System.out.println(result);
        return result;
    }

    /**
     * Get the item dropped when this cube is cut.
     *
     * @return AlienArtifact
     */
    @Override
    public Item getDroppedItem() {
        return new AlienArtifact();
    }

    /**
     * String representation.
     *
     * @return item name
     */
    @Override
    public String toString() {
        return "Alien Cube";
    }
}