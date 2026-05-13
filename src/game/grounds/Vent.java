package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Parasite;
import game.actors.Slime;
import game.enums.Ability;
import game.interfaces.Spawnable;
import game.interfaces.Spawner;
import game.status.PoisonStatus;

import java.util.Random;

/**
 * A ground tile that spawns a hostile creature when a contracted worker is nearby.
 * <p>
 * When activated, the vent creates either a slime or a parasite, places it on the
 * current location, triggers its spawn effect, and poisons nearby actors.
 */
public class Vent extends Ground implements Spawner {
    private static final int SPAWN_POISON_DURATION = 5;
    private static final int SPAWN_POISON_DAMAGE = 1;
    private static final int SPAWN_CHECK_RADIUS = 1;

    private final Random random = new Random();

    /**
     * Creates a vent ground tile.
     */
    public Vent() {
        super('v', "Vent");
    }

    /**
     * Spawns a hostile creature when a contracted worker is nearby.
     * <p>
     * If a worker is detected within the spawn-check radius, the vent creates a
     * random spawnable actor, places it at this location, invokes its spawn effect,
     * and then applies poison to nearby actors.
     *
     * @param location the location containing this vent
     */
    @Override
    public void tick(Location location) {
        if (!hasWorkerNearby(location)) {
            return;
        }

        if (location.containsAnActor()) {
            return;
        }

        Actor spawnedActor = spawn(location);
        boolean spawned = false;

        try {
            location.addActor(spawnedActor);

            Spawnable spawnable = spawnedActor.asCapability(Spawnable.class).orElse(null);
            if (spawnable != null) {
                spawnable.spawnEffect(location);
            }

            spawned = true;
        } catch (GameEngineException e) {
            System.out.println("Vent failed to spawn an actor: " + e.getMessage());
        }

        if (spawned) {
            poisonNearbyActors(location);
        }
    }

    /**
     * Check whether a worker is nearby.
     *
     * @param location the location containing this vent
     * @return true if a worker is within the check radius
     */
    private boolean hasWorkerNearby(Location location) {
        for (Location nearby : location.getNearbyLocations(SPAWN_CHECK_RADIUS)) {
            if (nearby.containsAnActor() && nearby.getActor().hasAbility(Ability.WORKER)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Poison all nearby actors after the vent spawns an actor.
     *
     * @param location the location containing this vent
     */
    private void poisonNearbyActors(Location location) {
        for (Location nearby : location.getNearbyLocations(SPAWN_CHECK_RADIUS)) {
            if (nearby.containsAnActor()) {
                Actor actor = nearby.getActor();
                actor.addStatus(new PoisonStatus(SPAWN_POISON_DURATION, SPAWN_POISON_DAMAGE));
            }
        }
    }

    /**
     * Creates a random spawnable actor for this vent.
     *
     * @param location the location where the actor will be spawned
     * @return either a new slime or a new parasite
     */
    @Override
    public Actor spawn(Location location) {
        if (random.nextBoolean()) {
            return Slime.getSlimeSpawn();
        }

        return Parasite.getParasiteSpawn();
    }
}