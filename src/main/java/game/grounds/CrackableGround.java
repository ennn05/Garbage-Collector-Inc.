package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Abstract base class for ground tiles that can be degraded by shockwaves.
 * <p>
 * Crackable ground tiles exist in a degradation chain: each call to {@link #degrade()}
 * returns the next stage. Subclasses define what that next stage is and how actors
 * are affected when they stand on this ground.
 * <p>
 * The degradation chain: {@link Floor} (level 0) → {@link CrackedFloor} (level 1)
 * → {@link RubbleField} (level 2).
 */
public abstract class CrackableGround extends Ground {
    private final int degradationLevel;

    /**
     * Creates a new crackable ground tile.
     *
     * @param displayChar           the character used to render this ground on the map
     * @param name                  the name of this ground type
     * @param initialDegradationLevel the degradation level of this ground stage (0 = intact)
     */
    protected CrackableGround(char displayChar, String name, int initialDegradationLevel) {
        super(displayChar, name);
        this.degradationLevel = initialDegradationLevel;
    }

    /**
     * Called when an actor steps onto this ground.
     * Subclasses define the effect — for example, cracked ground may collapse
     * under the actor's weight after a grace period.
     *
     * @param location the location of this ground tile
     * @param actor    the actor that stepped onto this tile
     */
    public abstract void onActorStep(Location location, Actor actor);

    /**
     * Returns the degradation level of this ground stage.
     * Higher values indicate more damage (e.g., 0 = Floor, 1 = CrackedFloor, 2 = RubbleField).
     *
     * @return the degradation level
     */
    public int getDegradationLevel() {
        return this.degradationLevel;
    }

    /**
     * Returns the next degradation stage of this ground.
     * May return {@code this} if the ground is already at maximum degradation.
     *
     * @return the degraded ground, or {@code this} if already fully degraded
     */
    public abstract Ground degrade();
}
