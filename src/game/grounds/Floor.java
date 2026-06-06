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
 *
 * @author Adrian Kristanto
 */
public class Floor extends CrackableGround implements Seedable {
    private static final int DEGRADATION_CHANCE = 50;
    private static final int DEGRADATION_LEVEL = 0;

    private final Random rand = new Random();

    public Floor() {
        super('_', "Floor", DEGRADATION_LEVEL);
    }

    @Override
    public void onActorStep(Location location, Actor actor) {}

    @Override
    public Ground degrade() {
        if (rand.nextInt(100) < DEGRADATION_CHANCE) {
            return this;
        }
        return new CrackedFloor();
    }
}
