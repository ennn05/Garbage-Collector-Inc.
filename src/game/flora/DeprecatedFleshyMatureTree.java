package game.flora;

import edu.monash.fit2099.engine.positions.Location;
import game.actors.ScrapSnatcher;

import java.util.Random;

/**
 * A 99-Deprecated variant of the Fleshy Mature Tree.
 * <p>
 * This mature tree spawns a Scrap Snatcher when a worker stands nearby. Every
 * 35 turns, it has a 50% chance to grow into a Fleshy Monolith.
 */
public class DeprecatedFleshyMatureTree extends FleshyTree {
    private static final int GROW_INTERVAL = 35;
    private static final double GROW_CHANCE = 0.50;

    private final Random random = new Random();

    /**
     * Creates a deprecated fleshy mature tree displayed as {@code Y}.
     */
    public DeprecatedFleshyMatureTree() {
        super('Y', "Deprecated Fleshy Mature Tree");
    }

    /**
     * Spawns a Scrap Snatcher on the first valid adjacent tile found.
     *
     * @param location the location of this mature tree
     */
    @Override
    protected void spawnActor(Location location) {
        spawnActorOnAdjacentTile(location, ScrapSnatcher::getScrapSnatcherSpawn);
    }

    /**
     * Every 35 turns, this mature tree has a 50% chance to grow into a Fleshy
     * Monolith.
     *
     * @param location the location of this mature tree
     * @return true if this tree grows this turn
     */
    @Override
    protected boolean tryGrow(Location location) {
        if (turnCount % GROW_INTERVAL == 0 && random.nextDouble() < GROW_CHANCE) {
            location.setGround(new FleshyMonolith());
            return true;
        }

        return false;
    }
}