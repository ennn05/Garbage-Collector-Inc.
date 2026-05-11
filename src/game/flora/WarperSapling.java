package game.flora;

import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Stage 1 of the Warper Tree (display: 'w').
 *
 * <p>Has no proximity effect. Every 20 turns there is a 25% chance it grows
 * into a {@link WarperMatureTree}.
 *
 * @author ennn12
 */
public class WarperSapling extends Tree {

    private static final int GROW_INTERVAL = 20;
    private static final double GROW_CHANCE = 0.25;
    private static final Random random = new Random();

    /** Constructor. */
    public WarperSapling() {
        super('w', "Warper Sapling");
    }

    /**
     * Every 20 turns, rolls a 25% chance to replace this sapling with a {@link WarperMatureTree}.
     *
     * @param location the location of this sapling
     * @return true if the sapling grew into a mature warper tree this turn
     */
    @Override
    protected boolean tryGrow(Location location) {
        if (turnCount % GROW_INTERVAL == 0 && random.nextDouble() < GROW_CHANCE) {
            location.setGround(new WarperMatureTree());
            return true;
        }
        return false;
    }
}
