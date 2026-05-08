package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A ground tile that represents fire. Actors that step on it take burn damage.
 * Fire disappears after 2 turns.
 */
public class Fire extends Ground {
    private int turnsRemaining;

    /**
     * Constructor for Fire tile.
     * @param turnsRemaining number of turns before fire disappears (typically 2)
     */
    public Fire(int turnsRemaining) {
        super('♥', "Fire");
        this.turnsRemaining = turnsRemaining;
    }

    /**
     * Default constructor with turnsRemaining = 2
     */
    public Fire() {
        this(2);
    }

    /**
     * Decrement the fire counter each turn.
     * When counter reaches 0, revert tile back to Floor.
     * @param location the location of this ground
     */
    @Override
    public void tick(Location location) {
        turnsRemaining--;
        if (turnsRemaining <= 0) {
            location.setGround(new Floor());
        }
    }

    /**
     * Apply BurnStatus to any actor entering this tile.
     * @param actor the actor entering the fire
     */
    @Override
    public void onActorEnter(Actor actor) {
        actor.addCapability(new BurnStatus(2, 1));
    }

    /**
     * Actors can always enter fire (they just take damage)
     * @param actor the actor to check
     * @return true
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }
}
