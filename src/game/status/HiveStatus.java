package game.status;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Host;
import game.interfaces.Spawnable;
import game.interfaces.Spawner;

/**
 * A status effect that causes a host to periodically spawn a new entity into an adjacent empty location.
 * <p>
 * Each tick, the host's hive effect is triggered. Once the spawn counter reaches zero,
 * the configured {@link Spawner} is spawned into the first available adjacent location.
 */
public class HiveStatus implements Status {
    private static final int SPAWNER_CHECK_RADIUS = 1;

    private final Spawner spawner;
    private final int spawnInterval;
    private int spawnCounter;

    /**
     * Creates a hive status that spawns the supplied entity at the given interval.
     *
     * @param spawner the entity factory used when spawning a new entity
     * @param spawnInterval the number of ticks between spawn attempts
     */
    public HiveStatus(Spawner spawner, int spawnInterval) {
        this.spawner = spawner;
        this.spawnInterval = spawnInterval;
        this.spawnCounter = spawnInterval;
    }

    /**
     * Advances this status by one tick.
     * <p>
     * If the current entity has the {@link Host} capability, its hive effect is triggered.
     * When the internal counter reaches zero, this status attempts to spawn a new entity
     * into the first adjacent location that does not already contain an actor.
     *
     * @param currEntity the entity currently carrying this status
     * @param location the location of the current entity
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        Host host = currEntity.asCapability(Host.class).orElse(null);

        if (host == null) {
            return;
        }

        host.hiveEffect();
        spawnCounter--;

        if (spawnCounter > 0) {
            return;
        }

        boolean spawned = false;

        for (Location nearby : location.getNearbyLocations(SPAWNER_CHECK_RADIUS)) {
            Actor spawnedActor = spawner.spawn(nearby);

            if (!nearby.containsAnActor() && nearby.getGround().canActorEnter(spawnedActor)) {
                try {
                    nearby.addActor(spawnedActor);

                    Spawnable spawnable = spawnedActor.asCapability(Spawnable.class).orElse(null);
                    if (spawnable != null) {
                        spawnable.spawnEffect(nearby);
                    }

                    spawned = true;
                    break;
                } catch (GameEngineException e) {
                    System.out.println("Failed to spawn entity: " + e.getMessage());
                }
            }
        }

        if (spawned) {
            spawnCounter = spawnInterval;
            System.out.println(currEntity + " spawned a new entity due to being a host.");
        }
    }

    /**
     * Hive status is always active while attached to an entity.
     *
     * @return true
     */
    @Override
    public boolean isStatusActive() {
        return true;
    }
}