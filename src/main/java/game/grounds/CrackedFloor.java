package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An intermediate ground stage in the degradation chain, between {@link Floor} and {@link RubbleField}.
 * <p>
 * When an actor first stands on a CrackedFloor, a one-turn grace period begins. If the actor
 * is still present on the next turn, the tile collapses into a {@link RubbleField}, which
 * immediately buries the actor.
 */
public class CrackedFloor extends CrackableGround {
    private static final int DEGRADATION_LEVEL = 1;
    private int gracePeriod = 1;

    /**
     * Creates a new CrackedFloor tile (display character: {@code Σ}).
     */
    public CrackedFloor() {
        super('Σ', "Cracked Floor", DEGRADATION_LEVEL);
    }

    /**
     * Each turn, checks whether an actor is present and delegates to {@link #onActorStep}.
     *
     * @param location the location of this tile
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            onActorStep(location, location.getActor());
        }
    }

    /**
     * Handles an actor standing on this cracked tile.
     * After the one-turn grace period expires, the tile collapses into a {@link RubbleField}
     * and the actor is buried.
     *
     * @param location the location of this tile
     * @param actor    the actor currently on this tile
     */
    @Override
    public void onActorStep(Location location, Actor actor) {
        if (this.gracePeriod <= 0) {
            RubbleField rubble = this.degrade();
            location.setGround(rubble);
            System.out.println(rubble.bury(actor, location.map()));
        }
        this.gracePeriod--;
    }

    /**
     * Degrades this CrackedFloor into a {@link RubbleField}.
     *
     * @return a new RubbleField
     */
    @Override
    public RubbleField degrade() {
        return new RubbleField();
    }
}
