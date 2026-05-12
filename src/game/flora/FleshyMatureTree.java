package game.flora;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Undead;
import game.interfaces.Spawnable;

import java.util.Objects;

/**
 * Stage 3 of the Fleshy Tree (display: 'Y').
 *
 * <p>Spawns an {@link Undead} on an adjacent tile when a worker steps nearby.
 * Does not grow further.
 *
 * @author ennn12
 */
public class FleshyMatureTree extends FleshyTree {

    /** Constructor. */
    public FleshyMatureTree() {
        super('Y', "Fleshy Mature Tree");
    }

    /**
     * Spawns an {@link Undead} on the first unoccupied adjacent tile found.
     *
     * @param location the location of this mature tree
     */
    @Override
    protected void spawnActor(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (!adjacent.containsAnActor() && adjacent.getGround().canActorEnter(new Undead())) {
                Undead undead = new Undead();
                location.map().addActor(undead, adjacent);
                Objects.requireNonNull(adjacent.getActorAs(Spawnable.class)).spawnEffect(adjacent);
                return;
            }
        }
    }
}
