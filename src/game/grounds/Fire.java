package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.FireHazard;

/**
 * A temporary fire that burns actors standing on it and disappears after a number of turns.
 */
public class Fire extends Ground implements FireHazard {
    public static final int DEFAULT_FIRE_DURATION = 5;
    private static final int DAMAGE_PER_TURN = 1;

    private final Ground previousGround;
    private int turnsRemaining;

    /**
     * Constructor for the standard fire effect.
     * The standard fire lasts for 5 turns.
     *
     * @param previousGround the ground that should be restored after the fire disappears
     */
    public Fire(Ground previousGround) {
        this(previousGround, DEFAULT_FIRE_DURATION);
    }

    /**
     * Constructor for fire with a custom duration.
     *
     * @param previousGround the ground that should be restored after the fire disappears
     * @param duration the number of turns this fire should remain on the ground
     */
    public Fire(Ground previousGround, int duration) {
        super('^', "Fire");
        this.previousGround = previousGround;
        this.turnsRemaining = duration;
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
            System.out.println(actor + " is burned by fire for " + DAMAGE_PER_TURN + " damage.");
        }

        turnsRemaining--;

        if (turnsRemaining > 0) {
            System.out.println("Fire at " + location + " has " + turnsRemaining + " turn(s) remaining.");
        }

        if (turnsRemaining <= 0) {
            location.setGround(previousGround);
            System.out.println("Fire at " + location + " has disappeared.");
        }
    }
}