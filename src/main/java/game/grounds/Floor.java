package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Seedable;

/**
 * Not lava. Not spikes. Not an elaborate trap. Just a perfectly flat surface
 * whose sole responsibility is preventing the {@code ContractedWorker} from
 * plummeting into the infinite vacuum of the Eclipse Nebula.
 *
 * @author Adrian Kristanto
 */
public class Floor extends CrackableGround implements Seedable {
    private static final int DEGRADATION_LEVEL = 0;

    /**
     * Creates a Floor tile (display character: {@code _}).
     * Implements {@link game.interfaces.Seedable} so fungal ground can spread here.
     */
    public Floor() {
        super('_', "Floor", DEGRADATION_LEVEL);
    }

    @Override
    public void onActorStep(Location location, Actor actor) {}

    @Override
    public CrackedFloor degrade() {
        return new CrackedFloor();
    }
}
