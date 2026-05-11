package game.flora;

import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Stage 2 of the Fleshy Tree (display: 'v').
 *
 * <p>Has no proximity effect. Every 25 turns there is a 50% chance it grows
 * into a {@link FleshyMatureTree}.
 *
 * <p>Extends {@link Tree} directly rather than {@link FleshyTree} because it
 * does not spawn actors — inheriting that contract would violate LSP.
 *
 * @author ennn12
 */
public class FleshySapling extends Tree {

    private static final int GROW_INTERVAL = 25;
    private static final double GROW_CHANCE = 0.50;
    private static final Random random = new Random();

    /** Constructor. */
    public FleshySapling() {
        super('v', "Fleshy Sapling");
    }

    /**
     * Every 25 turns, rolls a 50% chance to replace this sapling with a {@link FleshyMatureTree}.
     *
     * @param location the location of this sapling
     * @return true if the sapling grew into a mature tree this turn
     */
    @Override
    protected boolean tryGrow(Location location) {
        if (turnCount % GROW_INTERVAL == 0 && random.nextDouble() < GROW_CHANCE) {
            location.setGround(new FleshyMatureTree());
            return true;
        }
        return false;
    }
}
