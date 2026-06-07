package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Undead;
import game.interfaces.HoleGround;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The final stage in the ground degradation chain, produced when a {@link CrackedFloor} collapses.
 * <p>
 * A RubbleField:
 * <ul>
 *   <li>Cannot be entered by any actor.</li>
 *   <li>Buries an actor that was standing on the CrackedFloor when it collapsed.</li>
 *   <li>After {@value #MUMMIFIED_DURATION} turns, mummifies the buried actor into an {@link Undead},
 *       spawned at the nearest valid location using Chebyshev distance.</li>
 *   <li>Swallows all items present on its tile each turn.</li>
 *   <li>Fills any adjacent {@link HoleGround} tiles with a new RubbleField each turn.</li>
 * </ul>
 */
public class RubbleField extends CrackableGround {
    private static final int DEGRADATION_LEVEL = 2;
    private static final int MUMMIFIED_DURATION = 20;
    private Actor buriedActor = null;
    private int buryCounter = 0;

    /**
     * Creates a new RubbleField tile (display character: {@code Ʊ}).
     */
    public RubbleField() {
        super('Ʊ', "Rubble", DEGRADATION_LEVEL);
    }

    /**
     * Buries an actor under this rubble, removing them from the map.
     *
     * @param actor      the actor to bury
     * @param currentMap the map the actor currently occupies
     * @return a message describing the burial
     */
    public String bury(Actor actor, GameMap currentMap) {
        this.buriedActor = actor;
        currentMap.removeActor(actor);
        return actor + " is buried under " + this;
    }

    /**
     * Computes the Chebyshev (chessboard) distance between two map locations.
     *
     * @param location the candidate location
     * @param center   the reference location
     * @return the Chebyshev distance
     */
    private static int chebyshev(Location location, Location center) {
        int dx = Math.abs(location.x() - center.x());
        int dy = Math.abs(location.y() - center.y());
        return Math.max(dx, dy);
    }

    /**
     * Searches outward from {@code center} in expanding Chebyshev rings to find the
     * nearest unoccupied location that the given actor can enter.
     *
     * @param center the starting location (typically where the rubble is)
     * @param actor  the actor that will be spawned
     * @param map    the game map to search
     * @return an {@link Optional} containing the nearest valid location, or empty if none exists
     */
    private Optional<Location> findNearestSpawnLocation(Location center, Actor actor, GameMap map) {
        int maxRadius = Math.max(map.getXRange().max(), map.getYRange().max());
        for (int r = 1; r <= maxRadius; r++) {
            List<Location> allWithinR = center.getNearbyLocations(r);
            for (Location loc : allWithinR) {
                if (chebyshev(loc, center) != r) continue;
                if (loc.containsAnActor()) continue;
                if (!loc.getGround().canActorEnter(actor)) continue;
                return Optional.of(loc);
            }
        }
        return Optional.empty();
    }

    /**
     * Mummifies a buried actor by spawning an {@link Undead} at the nearest valid location.
     * If no valid spawn location can be found, or the engine rejects the spawn, a failure
     * message is returned instead.
     *
     * @param actor    the actor being mummified
     * @param location the location of this RubbleField (used as the search origin)
     * @return a message describing the outcome
     */
    public String mummify(Actor actor, Location location) {
        String result;
        try {
            Undead undead = Undead.getUndeadSpawn();
            Optional<Location> nearest = findNearestSpawnLocation(location, undead, location.map());
            if (nearest.isPresent()) {
                nearest.get().addActor(undead);
                undead.spawnEffect(nearest.get());
                result = actor + " reemerges as " + undead + " at " + nearest.get();
            } else {
                result = "Failed to spawn undead: no valid spawn location";
            }
        } catch (GameEngineException e) {
            result = "Failed to spawn undead: " + e.getMessage();
        }
        return result;
    }

    /**
     * RubbleField cannot be entered by any actor.
     *
     * @param actor the actor attempting to enter
     * @return always {@code false}
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn:
     * <ol>
     *   <li>Advances the burial counter and mummifies the buried actor after {@value #MUMMIFIED_DURATION} turns.</li>
     *   <li>Removes all items from this tile.</li>
     *   <li>Converts any adjacent {@link HoleGround} tiles into new RubbleFields.</li>
     * </ol>
     *
     * @param location the location of this tile
     */
    @Override
    public void tick(Location location) {
        if (this.buriedActor != null) {
            this.buryCounter++;
            if (this.buryCounter >= MUMMIFIED_DURATION) {
                System.out.println(mummify(this.buriedActor, location));
                this.buriedActor = null;
                this.buryCounter = 0;
            }
        }
        if (!location.getItems().isEmpty()) {
            for (Item item : new ArrayList<>(location.getItems())) {
                location.removeItem(item);
            }
        }
        List<Location> nearby = location.getNearbyLocations(1);
        for (Location adj : nearby) {
            HoleGround hole = adj.getGroundAs(HoleGround.class);
            if (hole != null) {
                adj.setGround(new RubbleField());
            }
        }
    }

    /**
     * Actors cannot step on RubbleField; this method has no effect.
     *
     * @param location the location of this tile
     * @param actor    the actor (never actually on this tile)
     */
    @Override
    public void onActorStep(Location location, Actor actor) {}

    /**
     * RubbleField is the final degradation stage and cannot degrade further.
     *
     * @return {@code this}
     */
    @Override
    public Ground degrade() {
        return this;
    }
}
