package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A temporary fire that burns actors standing on it and disappears after a few turns.
 */
public class Fire extends Ground {
    private static final int FIRE_DURATION = 5;
    private static final int DAMAGE_PER_TURN = 1;

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
            actor.hurt(DAMAGE_PER_TURN);
        }

        turnsRemaining--;

        if (turnsRemaining <= 0) {
            location.setGround(previousGround);
        }
    }
}