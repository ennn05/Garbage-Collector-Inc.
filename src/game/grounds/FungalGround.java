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

    protected FungalGround(char displayChar, String name) {
        super(displayChar, name);
    }

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

    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }
}
