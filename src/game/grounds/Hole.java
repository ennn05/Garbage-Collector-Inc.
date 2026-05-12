package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Parasite;
import game.actors.Slime;
import game.actors.Undead;
import game.interfaces.Spawnable;
import game.interfaces.Spawner;

import java.util.*;

/**
 * A hole that periodically spawns moon creatures.
 */
public class Hole extends Ground implements Spawner {
    private static final int SPAWN_INTERVAL = 20;
    private static final double GROW_CHANCE = 0.01;
    private static final int SPAWN_CHECK_RADIUS = 1;

    private final Random random = new Random();
    private int turnsElapsed;

    public Hole() {
        super('o', "Hole");
        this.turnsElapsed = 0;
    }

    /**
     * Count turns and attempt to spawn exactly once every 20 turns.
     * There is a 1% chance where the Hole expands to an adjacent ground.
     *
     * @param location the location of this ground
     */
    @Override
    public void tick(Location location) {
        turnsElapsed++;

        if (turnsElapsed < SPAWN_INTERVAL) {
            return;   // no generate
        }

        try {
            if (!location.containsAnActor()) { // empty then generate
                location.addActor(this.spawn(location));
                Objects.requireNonNull(location.getActorAs(Spawnable.class)).spawnEffect(location);
                if (random.nextDouble() < GROW_CHANCE) {
                    List<Location> adj = new ArrayList<>(location.getNearbyLocations(SPAWN_CHECK_RADIUS));
                    Collections.shuffle(adj); // shuffle for random adjacent
                    for (Location nearby : adj) {
                        if (nearby.getGroundAs(Hole.class) == null) {
                            nearby.setGround(new Hole());
                            break;
                        }
                    }
                }
            }
        } catch (GameEngineException | NullPointerException e) {
            throw new RuntimeException(e);
        }

        turnsElapsed = 0; // 20rounds attempt happened
    }

    @Override
    public Actor spawn(Location location) {
        if (random.nextBoolean()) {
            return Undead.getUndeadSpawn();
        } else {
            if (location.map().toString().equals("99-Deprecated")) {
                return Slime.getSlimeSpawn();
            } else if (location.map().toString().equals("20-Overflow")) {
                return Parasite.getParasiteSpawn();
            } else {
                return Undead.getUndeadSpawn(); // failsafe spawn Undead if map is not recognised
            }
        }
    }
}