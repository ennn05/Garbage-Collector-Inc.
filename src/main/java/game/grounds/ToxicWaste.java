package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.ToxicGround;

/**
 * A ground tile representing toxic waste. Actors standing on it take 1 damage per turn.
 * This tile is permanent and never reverts.
 */
public class ToxicWaste extends Ground implements ToxicGround {

    /**
     * Constructor for ToxicWaste tile.
     */
    public ToxicWaste() {
        super('≈', "Toxic Waste");
    }

    /**
     * Deal 1 damage per turn to any actor standing on this tile.
     * @param location the location of this ground
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.hurt(1);
        }
    }

    /**
     * Actors can move through toxic waste (they just take damage).
     * @param actor the actor to check
     * @return true
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }
}
