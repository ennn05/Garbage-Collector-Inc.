package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Slime;
import game.actors.Undead;

import java.util.Random;

/**
 * A hole that periodically spawns moon creatures.
 */
public class Hole extends Ground {
    private static final int SPAWN_INTERVAL = 20;
    private final Random random = new Random();
    private int turnsElapsed;

    public Hole() {
        super('o', "Hole");
        this.turnsElapsed = 0;
    }

    /**
     * Count turns and attempt to spawn exactly once every 20 turns.
     *
     * @param location the location of this ground
     */
    @Override
    public void tick(Location location) {
        turnsElapsed++;

        if (turnsElapsed < SPAWN_INTERVAL) {
            return;   // no generate
        }

        Actor spawnedActor;    // spawn actor save

        if (random.nextBoolean()) {     // 50%
            spawnedActor = new Undead();
        } else {
            spawnedActor = new Slime();
        }

        try {
            if (!location.containsAnActor()) { // empty then generate
                location.addActor(spawnedActor);
            }
        } catch (GameEngineException e) {
            // fail then skip
        }

        turnsElapsed = 0; // 20rounds attempt happened
    }
}