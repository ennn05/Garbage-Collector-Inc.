package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.SporeEmitter;

import java.util.Random;

/**
 * A status applied when an actor walks through BlightFungus.
 * Each tick there is a 30% chance the infected entity spreads BlightFungus
 * on its current tile via its SporeEmitter implementation.
 */
public class SporeInfection implements Status {

    private static final int SPREAD_CHANCE = 30;
    private int turnsRemaining;
    private final Random random = new Random();

    /**
     * Creates a SporeInfection status that lasts for the given number of turns.
     *
     * @param turnsRemaining how many turns the infection persists before clearing
     */
    public SporeInfection(int turnsRemaining) {
        this.turnsRemaining = turnsRemaining;
    }

    /**
     * Called once per turn while this status is active. Attempts to spread spores
     * via the entity's {@link SporeEmitter} capability with a {@value #SPREAD_CHANCE}%
     * probability, then decrements the turn counter. Logs expiry when the counter
     * reaches zero.
     *
     * @param currEntity the infected entity
     * @param location   the entity's current location
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        currEntity.asCapability(SporeEmitter.class).ifPresent(emitter -> {
            if (random.nextInt(100) < SPREAD_CHANCE) {
                emitter.emitSpores(location);
            }
        });

        turnsRemaining--;
        System.out.println(currEntity + " spore infection: " + turnsRemaining + " turn(s) remaining.");

        if (turnsRemaining == 0) {
            System.out.println(currEntity + "'s spore infection has cleared.");
        }
    }

    /**
     * Returns {@code true} while turns remain; the engine removes this status once
     * it returns {@code false}.
     *
     * @return {@code true} if the infection has not yet expired
     */
    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }
}
