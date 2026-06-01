package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Slime;
import game.enums.Ability;
import game.interfaces.Spawnable;

/**
 * A dense, impassable mass of interlocked fungal stalks.
 *
 * - Blocks all actor movement (canActorEnter returns false).
 * - Every 25 turns, spawns a creature on an adjacent tile when a worker is nearby.
 *   TODO (REQ2): Replace Slime spawn with ScrapSnatcher once REQ2 is implemented.
 * - TODO (REQ1): Implement Cuttable interface when PlasmaCutter is added.
 *   When cut: drops a SporeCanister and converts itself to a Floor tile.
 */
public class SporeColony extends FungalGround {

    private static final int SPAWN_INTERVAL = 25;
    private static final int WORKER_CHECK_RADIUS = 1;
    private int spawnTimer = 0;

    public SporeColony() {
        super('§', "SporeColony");
    }

    @Override
    public void tick(Location location) {
        spawnTimer++;
        if (spawnTimer >= SPAWN_INTERVAL) {
            if (hasWorkerNearby(location)) {
                spawnCreature(location);
            }
            spawnTimer = 0;
        }
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    protected void onActorPresent(Actor actor, Location location) {
        // Actors cannot enter SporeColony; this is never called.
    }

    @Override
    protected void spread(Location location) {
        // SporeColony does not spread to adjacent tiles.
    }

    private boolean hasWorkerNearby(Location location) {
        for (Location nearby : location.getNearbyLocations(WORKER_CHECK_RADIUS)) {
            if (nearby.containsAnActor() && nearby.getActor().hasAbility(Ability.WORKER)) {
                return true;
            }
        }
        return false;
    }

    private void spawnCreature(Location location) {
        for (Location nearby : location.getNearbyLocations(WORKER_CHECK_RADIUS)) {
            Actor candidate = Slime.getSlimeSpawn();
            if (!nearby.containsAnActor() && nearby.getGround().canActorEnter(candidate)) {
                try {
                    nearby.addActor(candidate);
                    candidate.asCapability(Spawnable.class)
                            .ifPresent(spawnable -> spawnable.spawnEffect(nearby));
                    System.out.println("SporeColony spawns a creature at " + nearby + "!");
                    return;
                } catch (GameEngineException e) {
                    System.out.println("SporeColony failed to spawn: " + e.getMessage());
                }
            }
        }
    }
}
