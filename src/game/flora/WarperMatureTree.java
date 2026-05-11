package game.flora;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.WarpAction;

/**
 * Stage 2 of the Warper Tree (display: 'W').
 *
 * <p>When any worker steps into an adjacent tile, the tree's roots grab them and
 * automatically warp them to a random location on the map via {@link WarpAction}.
 * Does not grow further.
 *
 * @author ennn12
 */
public class WarperMatureTree extends Tree {

    /** Constructor. */
    public WarperMatureTree() {
        super('W', "Warper Mature Tree");
    }

    /**
     * Scans adjacent tiles for an actor. On first match, forces a random warp via {@link WarpAction}.
     *
     * @param location the location of this mature warper tree
     * @return true if an actor was found and warped this turn
     */
    @Override
    protected boolean proximityEffect(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) {
                Actor actor = adjacent.getActor();
                new WarpAction().execute(actor, location.map());
                return true;
            }
        }
        return false;
    }
}
