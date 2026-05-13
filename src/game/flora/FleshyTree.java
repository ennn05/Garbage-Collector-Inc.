package game.flora;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

/**
 * Abstract base for fleshy trees that spawn an actor when any worker enters an adjacent tile.
 *
 * <p>Centralises the adjacency-check logic shared by {@link FleshySprout} and
 * {@link FleshyMatureTree}, keeping each subclass responsible only for deciding
 * <em>what</em> to spawn rather than <em>how</em> to detect proximity (DRY).
 *
 * @author ennn12
 */
public abstract class FleshyTree extends Tree {

    /**
     * Constructor.
     *
     * @param displayChar character used to display this tree stage
     * @param name        human-readable name of this tree stage
     */
    public FleshyTree(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Scans adjacent tiles for workers only. On first match, delegates to
     * {@link #spawnActor(Location)} and returns true so growth is skipped this turn.
     *
     * @param location the location of this tree
     * @return true if an adjacent worker was found and a spawn was triggered
     */
    @Override
    protected boolean proximityEffect(Location location) {
        for (Exit exit : location.getExits()) {
            Location surroundingLocation = exit.getDestination();

            if (surroundingLocation.containsAnActor()
                    && surroundingLocation.getActor().hasAbility(Ability.WORKER)) {
                spawnActor(location);
                return true;
            }
        }
        return false;
    }

    /**
     * Places a new actor on an unoccupied adjacent tile.
     * Concrete subclasses define which actor to create.
     *
     * @param location the location of this tree
     */
    protected abstract void spawnActor(Location location);
}