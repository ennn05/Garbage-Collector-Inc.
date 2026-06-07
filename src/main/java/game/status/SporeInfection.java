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

    public SporeInfection(int turnsRemaining) {
        this.turnsRemaining = turnsRemaining;
    }

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

    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }
}
