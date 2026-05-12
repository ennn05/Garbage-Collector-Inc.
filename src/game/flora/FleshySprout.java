package game.flora;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Slime;
import game.interfaces.Spawnable;

import java.util.Objects;
import java.util.Random;

/**
 * Stage 1 of the Fleshy Tree (display: 'y').
 *
 * <p>Spawns a {@link Slime} on an adjacent tile when a worker steps nearby.
 * Every 20 turns there is a 25% chance it grows into a {@link FleshySapling}.
 *
 * @author ennn12
 */
public class FleshySprout extends FleshyTree {

    private static final int GROW_INTERVAL = 20;
    private static final double GROW_CHANCE = 0.25;
    private static final Random random = new Random();

    /**
     * Creates a Fleshy Sprout, the first growth stage of a Fleshy Tree,
     * displayed as 'y' on the map.
     */
    public FleshySprout() {
        super('y', "Fleshy Sprout");
    }

    /**
     * Spawns a {@link Slime} on the first unoccupied adjacent tile found.
     *
     * @param location the location of this sprout
     */
    @Override
    protected void spawnActor(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (!adjacent.containsAnActor() && adjacent.getGround().canActorEnter(new Slime())) {
                Slime slime = new Slime();
                location.map().addActor(slime, adjacent);
                Objects.requireNonNull(adjacent.getActorAs(Spawnable.class)).spawnEffect(adjacent);
                return;
            }
        }
    }

    /**
     * Every 20 turns, rolls a 25% chance to replace this sprout with a {@link FleshySapling}.
     *
     * @param location the location of this sprout
     * @return true if the sprout grew into a sapling this turn
     */
    @Override
    protected boolean tryGrow(Location location) {
        if (turnCount % GROW_INTERVAL == 0 && random.nextDouble() < GROW_CHANCE) {
            location.setGround(new FleshySapling());
            return true;
        }
        return false;
    }
}
