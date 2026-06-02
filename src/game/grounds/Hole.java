package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Parasite;
import game.actors.ScrapSnatcher;
import game.actors.Slime;
import game.actors.Undead;
import game.interfaces.HoleGround;
import game.interfaces.Spawnable;
import game.interfaces.Spawner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A hole that periodically spawns moon creatures.
 */
public class Hole extends Ground implements Spawner, HoleGround {
    private static final int SPAWN_INTERVAL = 20;
    private static final double GROW_CHANCE = 0.01;
    private static final int SPAWN_CHECK_RADIUS = 1;
    private static final String DEPRECATED_MAP_NAME = "99-Deprecated";
    private static final String OVERFLOW_MAP_NAME = "20-Overflow";

    private final Random random = new Random();
    private int turnsElapsed;

    /**
     * Creates a hole ground tile.
     */
    public Hole() {
        super('o', "Hole");
        this.turnsElapsed = 0;
    }

    /**
     * Counts turns and attempts to spawn exactly once every 20 turns.
     * There is a 1% chance that the Hole expands to an adjacent non-hole ground.
     *
     * @param location the location of this ground
     */
    @Override
    public void tick(Location location) {
        turnsElapsed++;

        if (turnsElapsed < SPAWN_INTERVAL) {
            return;
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
                applySpawnEffect(spawnedActor, location);
                tryGrow(location);
            } catch (GameEngineException ignored) {
                // The spawn attempt is safely ignored if the engine rejects the actor placement.
            }
        }

        turnsElapsed = 0;
    }

    /**
     * Creates a random creature for this hole.
     * <p>
     * On 99-Deprecated, the Hole may now spawn a Scrap Snatcher in addition to
     * the previous creatures. On 20-Overflow, the previous behaviour is retained.
     *
     * @param location the location of the spawner
     * @return a newly spawned actor
     */
    @Override
    public Actor spawn(Location location) {
        List<Supplier<Actor>> spawnOptions = new ArrayList<>();
        spawnOptions.add(Undead::getUndeadSpawn);

        String mapName = location.map().toString();
        if (DEPRECATED_MAP_NAME.equals(mapName)) {
            spawnOptions.add(Slime::getSlimeSpawn);
            spawnOptions.add(ScrapSnatcher::getScrapSnatcherSpawn);
        } else if (OVERFLOW_MAP_NAME.equals(mapName)) {
            spawnOptions.add(Parasite::getParasiteSpawn);
        }

        return spawnOptions.get(random.nextInt(spawnOptions.size())).get();
    }

    /**
     * Applies the spawn side effect if the spawned actor supports it.
     *
     * @param spawnedActor the actor that has just been spawned
     * @param location the spawn location
     */
    private void applySpawnEffect(Actor spawnedActor, Location location) {
        Spawnable spawnable = spawnedActor.asCapability(Spawnable.class).orElse(null);
        if (spawnable != null) {
            spawnable.spawnEffect(location);
        }
    }

    /**
     * Attempts to grow another hole on a nearby non-hole ground.
     *
     * @param location the location of this hole
     */
    private void tryGrow(Location location) {
        if (random.nextDouble() >= GROW_CHANCE) {
            return;
        }

        List<Location> nearbyLocations = new ArrayList<>(location.getNearbyLocations(SPAWN_CHECK_RADIUS));
        Collections.shuffle(nearbyLocations);

        for (Location nearby : nearbyLocations) {
            if (nearby.getGround().getDisplayChar() != this.getDisplayChar()) {
                nearby.setGround(new Hole());
                return;
            }
        }
    }
}