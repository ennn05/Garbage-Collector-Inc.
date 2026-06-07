package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;
import game.interfaces.SporeEmitter;

/**
 * Abstract base for all fungal terrain tiles.
 * Each turn: checks for actors on the tile (delegating to subclasses), then
 * triggers AoE spread if a non-worker SporeEmitter (e.g. Undead) is present,
 * and ticks a passive spread timer.
 */
public abstract class FungalGround extends Ground {

    private static final int SPREAD_INTERVAL = 10;
    private int spreadTimer = 0;

    /**
     * Initialises the fungal ground tile with the given display character and name.
     *
     * @param displayChar the character used to represent this tile on the map
     * @param name        the human-readable name of this tile type
     */
    protected FungalGround(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Called every turn. Delegates to {@link #onActorPresent} when an actor is
     * standing on this tile, triggers AoE spread via any non-worker
     * {@link SporeEmitter} present, then ticks the passive spread timer.
     *
     * @param location the location of this tile
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            onActorPresent(actor, location);

            // If the ground was replaced (e.g. SporeExplosion self-destructed), stop.
            if (location.getGroundAs(FungalGround.class) == null) {
                return;
            }

            // Non-worker SporeEmitters (e.g. Undead) trigger AoE spread on any FungalGround.
            if (!actor.hasAbility(Ability.WORKER)) {
                actor.asCapability(SporeEmitter.class)
                        .ifPresent(emitter -> emitter.emitSpores(location));
            }
        }

        spreadTimer++;
        if (spreadTimer >= SPREAD_INTERVAL) {
            spread(location);
            spreadTimer = 0;
        }
    }

    /**
     * Called each turn an actor is standing on this tile.
     *
     * @param actor    the actor on this tile
     * @param location the location of this tile
     */
    protected abstract void onActorPresent(Actor actor, Location location);

    /**
     * Called every {@code SPREAD_INTERVAL} turns to expand this fungal ground
     * to an adjacent tile.
     *
     * @param location the location of this tile
     */
    protected abstract void spread(Location location);

    /**
     * Immediately triggers a spread and resets the passive spread timer.
     * Called by external systems (e.g. {@code FungalBloomEffect}) that need
     * to accelerate fungal growth outside the normal tick cycle.
     *
     * @param location the location of this tile
     */
    public void forceSpread(Location location) {
        spread(location);
        spreadTimer = 0;
    }

    /**
     * All actors may enter a fungal ground tile.
     *
     * @param actor the actor attempting to enter
     * @return {@code true}
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }
}
