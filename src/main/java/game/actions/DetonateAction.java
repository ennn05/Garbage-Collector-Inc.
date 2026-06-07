package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.TremorBeacon;

import java.util.List;

/**
 * Action that detonates all {@link TremorBeacon} items within a given range of the acting actor.
 * <p>
 * Each beacon found within range is removed from the map and its
 * {@link TremorBeacon#triggerShockwave} is fired at the beacon's location. Multiple beacons
 * detonate simultaneously in a single action.
 */
public class DetonateAction extends Action {
    private final int range;

    /**
     * Creates a DetonateAction that searches for beacons within the given radius.
     *
     * @param range the search radius in tiles
     */
    public DetonateAction(int range) {
        this.range = range;
    }

    /**
     * Finds all TremorBeacons within {@code range} tiles of the actor, removes them,
     * and triggers each beacon's shockwave at its map location.
     *
     * @param actor the actor using the detonator
     * @param map   the current game map
     * @return a message confirming detonation
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        List<Location> nearby = map.locationOf(actor).getNearbyLocations(range);
        for (Location location : nearby) {
            List<TremorBeacon> beacons = location.getItemsAs(TremorBeacon.class);
            for (TremorBeacon beacon : beacons) {
                location.removeItem(beacon);
                beacon.triggerShockwave(location, map);
            }
        }
        return "Nearby beacons detonated.";
    }

    /**
     * {@inheritDoc}
     *
     * @param actor the actor considering this action
     * @return a description of the action for the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " detonates all nearby beacons.";
    }
}
