package game.flora;

import edu.monash.fit2099.engine.positions.Location;
import game.actors.Undead;

import java.util.Random;

/**
 * A 99-Deprecated variant of the Fleshy Sprout.
 * <p>
 * This sprout spawns an Undead when a worker stands nearby. It skips the sapling
 * stage and may grow directly into a Deprecated Fleshy Mature Tree every 20 turns.
 */
public class DeprecatedFleshySprout extends FleshyTree {
    private static final int GROW_INTERVAL = 20;
    private static final double GROW_CHANCE = 0.25;

    private final Random random = new Random();

    /**
     * Creates a deprecated fleshy sprout displayed as {@code y}.
     */
    public DeprecatedFleshySprout() {
        super('y', "Deprecated Fleshy Sprout");
    }

    /**
     * Spawns an Undead on the first valid adjacent tile found.
     *
     * @param location the location of this sprout
     */
    @Override
    protected void spawnActor(Location location) {
        spawnActorOnAdjacentTile(location, Undead::getUndeadSpawn);
    }

    /**
     * Every 20 turns, this sprout has a 25% chance to grow directly into a
     * Deprecated Fleshy Mature Tree.
     *
     * @param location the location of this sprout
     * @return true if this sprout grows this turn
     */
    @Override
    protected boolean tryGrow(Location location) {
        if (turnCount % GROW_INTERVAL == 0 && random.nextDouble() < GROW_CHANCE) {
            location.setGround(new DeprecatedFleshyMatureTree());
            return true;
        }

        return false;
    }
}