package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Seedable;

import java.util.Random;

/**
 * Not lava. Not spikes. Not an elaborate trap. Just a perfectly flat surface
 * whose sole responsibility is preventing the {@code ContractedWorker} from
 * plummeting into the infinite vacuum of the Eclipse Nebula.
 * <p>
 * When hit by a shockwave ({@link #degrade()}), a Floor has a 50% chance to
 * remain intact and a 50% chance to crack into a {@link CrackedFloor}.
 *
 * @author Adrian Kristanto
 */
public class Floor extends CrackableGround implements Seedable {
    private static final int DEGRADATION_CHANCE = 50;
    private static final int DEGRADATION_LEVEL = 0;

    private final Random rand = new Random();

    /**
     * Creates a Floor tile (display character: {@code _}).
     * Implements {@link game.interfaces.Seedable} so fungal ground can spread here.
     */
    public Floor() {
        super('_', "Floor", DEGRADATION_LEVEL);
    }

    /**
     * Actors stepping on Floor have no side effects.
     *
     * @param location the location of this tile
     * @param actor    the actor standing on this tile
     */
    @Override
    public void onActorStep(Location location, Actor actor) {}

    /**
     * Degrades this Floor when hit by a shockwave.
     * Returns {@code this} with 50% probability; otherwise returns a new {@link CrackedFloor}.
     *
     * @return the same Floor (50%) or a new CrackedFloor (50%)
     */
    @Override
    public Ground degrade() {
        if (rand.nextInt(100) < DEGRADATION_CHANCE) {
            return this;
        }
        return new CrackedFloor();
    }
}
