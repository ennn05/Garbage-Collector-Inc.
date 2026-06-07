package game.flora;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Abstract base class for all mutated flora on the 20-Overflow moon.
 *
 * <p>Uses the Template Method pattern in {@link #tick(Location)} to enforce the
 * single-action-per-turn rule: a proximity effect takes priority over growth,
 * so a tree can never both spawn/warp and grow in the same turn.
 *
 * @author ennn12
 */
public abstract class Tree extends Ground {

    /** Number of turns this tree has been alive. */
    protected int turnCount;

    /**
     * Constructor.
     *
     * @param displayChar character used to display this tree on the map
     * @param name        human-readable name of this tree stage
     */
    public Tree(char displayChar, String name) {
        super(displayChar, name);
        this.turnCount = 0;
    }

    /**
     * Template method called once per turn.
     * Attempts a proximity effect first; growth is only tried if no proximity effect fired.
     *
     * @param location the location of this tree
     */
    @Override
    public void tick(Location location) {
        turnCount++;
        if (!proximityEffect(location)) {
            tryGrow(location);
        }
    }

    /**
     * Hook for proximity-triggered effects (spawning actors, warping workers).
     * Default does nothing.
     *
     * @param location the location of this tree
     * @return true if an effect was triggered this turn
     */
    protected boolean proximityEffect(Location location) {
        return false;
    }

    /**
     * Hook for periodic growth to the next stage.
     * Default does nothing.
     *
     * @param location the location of this tree
     * @return true if the tree advanced to its next stage this turn
     */
    protected boolean tryGrow(Location location) {
        return false;
    }
}
