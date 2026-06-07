package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.FungalGround;

/**
 * Implemented by entities that can actively seed FungalGround tiles onto the map.
 * Each implementor determines when and where spores are released and which
 * FungalGround subtype is seeded.
 */
public interface SporeEmitter {

    /**
     * Release spores at the given location, potentially transforming nearby
     * Floor tiles into a FungalGround subtype.
     *
     * @param source the location from which spores are emitted
     */
    void emitSpores(Location source);

    /**
     * Returns the FungalGround subtype that this emitter seeds.
     *
     * @return the class of the FungalGround subtype
     */
    Class<? extends FungalGround> getSporeType();
}
