package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.FireHazard;
import game.status.BurnStatus;

/**
 * A temporary fire that burns actors standing on it and disappears after a few turns.
 */
public class Fire extends Ground implements FireHazard {
    private static final int FIRE_DURATION = 2;

    // "Standard burn effect" used elsewhere in this codebase (e.g. Lantern sale).
    private static final int BURN_TURNS = 3;
    private static final int BURN_DAMAGE = 2;

    private final Ground previousGround;
    private int turnsRemaining;

    public Fire(Ground previousGround) {
        super('^', "Fire");
        this.previousGround = previousGround;
        this.turnsRemaining = FIRE_DURATION;
    }

    /**
     * Burn any actor standing on the fire and count down the remaining duration.
     * When the fire ends, restore the previous ground.
     *
     * @param location the location of the fire
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            if (!actor.hasStatus(BurnStatus.class)) {
                actor.addStatus(new BurnStatus(BURN_TURNS, BURN_DAMAGE));
            }
        }

        turnsRemaining--;

        if (turnsRemaining > 0) {
            System.out.println("Fire at " + location + " has " + turnsRemaining + " turn(s) remaining.");
        }

        if (turnsRemaining <= 0) {
            location.setGround(previousGround);
        }
    }
}
