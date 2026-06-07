package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.interfaces.TeleportAnchor;
import game.interfaces.Teleportable;
import game.items.Flask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A magic circle that allows within-map teleportation to another magic circle.
 * Using it randomly selects another magic circle on the map as the destination.
 * After teleporting, a Flask is spawned on an adjacent enterable tile.
 */
public class MagicCircle extends Ground implements Teleportable, TeleportAnchor {
    private final Random random = new Random();

    /**
     * Constructor for MagicCircle.
     */
    public MagicCircle() {
        super('◎', "Magic Circle");
    }

    /**
     * Get a single randomly-selected other magic circle on the map.
     * Per REQ2, the game randomly teleports the actor, so the actor has no choice.
     *
     * @param map the current game map
     * @return list with a single random magic circle destination, or an empty list if none exists
     */
    @Override
    public List<Location> getDestinations(GameMap map) {
        List<Location> circles = new ArrayList<>();
        Location thisLocation = null;

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location location = map.at(x, y);

                if (location != null && location.getGround() == this) {
                    thisLocation = location;
                }

                if (location != null
                        && !location.containsAnActor()
                        && location.getGroundAs(TeleportAnchor.class) != null) {
                    circles.add(location);
                }
            }
        }

        if (thisLocation != null) {
            circles.remove(thisLocation);
        }

        List<Location> result = new ArrayList<>();
        if (!circles.isEmpty()) {
            result.add(circles.get(random.nextInt(circles.size())));
        }

        return result;
    }

    /**
     * When teleported via MagicCircle, spawn a Flask on an adjacent enterable tile.
     *
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the destination location
     * @param map the current game map
     */
    @Override
    public void onTeleport(Actor actor, Location source, Location destination, GameMap map) {
        spawnFlaskOnAdjacentTile(destination, actor);
    }

    /**
     * Get actions available at this location.
     *
     * @param actor the actor
     * @param location the location of this ground
     * @param direction the direction of this ground from the actor
     * @return action list containing TeleportAction
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (!direction.isEmpty()) {
            return actions;
        }
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
     *
     * @param actor the actor
     * @return true
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Spawn a Flask on an adjacent enterable tile.
     *
     * @param location the location to search from
     * @param actor the actor that used the Magic Circle
     */
    private void spawnFlaskOnAdjacentTile(Location location, Actor actor) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();

            if (!adjacent.containsAnActor() && adjacent.canActorEnter(actor)) {
                adjacent.addItem(new Flask());
                return;
            }
        }
    }
}