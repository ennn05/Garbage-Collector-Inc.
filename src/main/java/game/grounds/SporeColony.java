package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ScrapSnatcher;
import game.enums.Ability;
import game.interfaces.Cuttable;
import game.interfaces.Spawnable;
import game.items.SporeCanister;

/**
 * A dense, impassable mass of interlocked fungal stalks.
 *
 * - Blocks all actor movement (canActorEnter returns false).
 * - Every 25 turns, spawns a ScrapSnatcher on an adjacent tile when a worker is nearby.
 * - Can be cut with a Plasma Cutter: drops a SporeCanister and becomes a Floor tile.
 */
public class SporeColony extends FungalGround implements Cuttable {

    private static final int SPAWN_INTERVAL = 25;
    private static final int WORKER_CHECK_RADIUS = 1;
    private int spawnTimer = 0;

    /**
     * Creates a SporeColony tile (display character: {@code §}).
     */
    public SporeColony() {
        super('§', "SporeColony");
    }

    /**
     * Advances the spawn timer each turn. Every {@value #SPAWN_INTERVAL} turns,
     * spawns a creature on an adjacent tile if a worker is within {@value #WORKER_CHECK_RADIUS} tile.
     *
     * @param location the location of this tile
     */
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

    /**
     * Always returns {@code false} — the dense stalk mass is impassable to all actors.
     *
     * @param actor the actor attempting to enter
     * @return {@code false}
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * No-op: actors cannot enter a SporeColony, so this callback is never triggered.
     *
     * @param actor    the actor (never present on this tile)
     * @param location the location of this tile
     */
    @Override
    protected void onActorPresent(Actor actor, Location location) {
        // Actors cannot enter SporeColony; this is never called.
    }

    /**
     * No-op: SporeColony does not spread passively to adjacent tiles.
     * Creature spawning is handled by {@link #tick(Location)} instead.
     *
     * @param location the location of this tile
     */
    @Override
    protected void spread(Location location) {
        // SporeColony does not spread to adjacent tiles.
    }

    /**
     * Returns {@code true} if any actor with the {@link Ability#WORKER} capability
     * is within {@value #WORKER_CHECK_RADIUS} tile of the given location.
     *
     * @param location the location to search around
     * @return {@code true} if a worker is adjacent
     */
    private boolean hasWorkerNearby(Location location) {
        for (Location nearby : location.getNearbyLocations(WORKER_CHECK_RADIUS)) {
            if (nearby.containsAnActor() && nearby.getActor().hasAbility(Ability.WORKER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to place a new {@link ScrapSnatcher} on an adjacent walkable tile.
     * Tries every exit in order and returns after the first successful placement.
     *
     * @param location the location of this SporeColony tile
     */
    private void spawnCreature(Location location) {
        for (Location nearby : location.getNearbyLocations(WORKER_CHECK_RADIUS)) {
            Actor candidate = ScrapSnatcher.getScrapSnatcherSpawn();
            if (!nearby.containsAnActor() && nearby.getGround().canActorEnter(candidate)) {
                try {
                    nearby.addActor(candidate);
                    candidate.asCapability(Spawnable.class)
                            .ifPresent(spawnable -> spawnable.spawnEffect(nearby));
                    System.out.println("SporeColony spawns a Scrap Snatcher at " + nearby + "!");
                    return;
                } catch (GameEngineException e) {
                    System.out.println("SporeColony failed to spawn: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Always returns {@code true} — a SporeColony can be cut by any actor carrying
     * a Plasma Cutter.
     *
     * @param actor the actor attempting to cut
     * @return {@code true}
     */
    @Override
    public boolean canBeCut(Actor actor) {
        return true;
    }

    /**
     * Replaces this tile with a {@link game.grounds.Floor} and logs the result.
     * The item drop is handled separately via {@link #getDroppedItem()}.
     *
     * @param actor    the actor performing the cut
     * @param location the location of this tile
     * @return a message describing the outcome
     */
    @Override
    public String onCut(Actor actor, Location location) {
        String result = actor + " cuts through the SporeColony with the Plasma Cutter! " +
                "The fungal mass collapses into a Floor tile.";
        System.out.println(result);
        return result;
    }

    /**
     * Returns the item dropped when this colony is cut — always a {@link SporeCanister}.
     *
     * @return a new {@link SporeCanister}
     */
    @Override
    public Item getDroppedItem() {
        return new SporeCanister();
    }
}
