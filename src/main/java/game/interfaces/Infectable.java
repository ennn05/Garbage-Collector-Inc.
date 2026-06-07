package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Represents an entity that can be infected by another actor.
 */
public interface Infectable {
    /**
     * Performs the infection side effect on the actor.
     *
     * @param otherActor the other Actor that is trying to infect this Actor
     * @param gameMap the game map that the Actor is on
     * @return a String describing the result of the infection attempt
     */
    String infect(Actor otherActor, GameMap gameMap);
}
