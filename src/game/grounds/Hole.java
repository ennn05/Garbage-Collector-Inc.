package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Parasite;
import game.actors.Slime;
import game.actors.Undead;
import game.interfaces.HoleGround;
import game.interfaces.Spawnable;
import game.interfaces.Spawner;

import java.util.*;

/**
 * A hole that periodically spawns moon creatures.
 */
public class Hole extends Ground implements Spawner, HoleGround {
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

        if (!location.containsAnActor()) {
            Actor spawnedActor = this.spawn(location);
            try {
                location.addActor(spawnedActor);
            } catch (GameEngineException e) {
                System.out.println("Hole failed to spawn actor: " + e.getMessage());
                return;
            }

            Spawnable spawnable = spawnedActor.asCapability(Spawnable.class).orElse(null);
            if (spawnable != null) {
                spawnable.spawnEffect(location);
            }

            if (random.nextDouble() < GROW_CHANCE) {
                List<Location> adj = new ArrayList<>(location.getNearbyLocations(SPAWN_CHECK_RADIUS));
                Collections.shuffle(adj);
                for (Location nearby : adj) {
                    if (nearby.getGroundAs(HoleGround.class) == null) {
                        nearby.setGround(new Hole());
                        break;
                    }
                }
            }
        }

        turnsElapsed = 0; // 20rounds attempt happened
    }

    /**
     * Spawns either an Undead or Slime if the map is 99-Deprecated.
     * Spawns either an Undead or Parasite if the map is 20-Overflow.
     *
     * @param location the location of the spawner
     * @return the spawned actor
     */
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