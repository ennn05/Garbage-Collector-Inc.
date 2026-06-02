package game.flora;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;
import game.interfaces.Spawnable;

import java.util.function.Supplier;

/**
 * Abstract base for fleshy trees that spawn an actor when any worker enters an adjacent tile.
 *
 * @author ennn12
 */
public abstract class FleshyTree extends Tree {

    /**
     * Constructor.
     *
     * @param displayChar character used to display this tree stage
     * @param name        human-readable name of this tree stage
     */
    public FleshyTree(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Scans adjacent tiles for workers only. On first match, delegates to
     * {@link #spawnActor(Location)} and returns true so growth is skipped this turn.
     *
     * @param location the location of this tree
     * @return true if an adjacent worker was found and a spawn was triggered
     */
    @Override
    protected boolean proximityEffect(Location location) {
        for (Exit exit : location.getExits()) {
            Location surroundingLocation = exit.getDestination();

            if (surroundingLocation.containsAnActor()
                    && surroundingLocation.getActor().hasAbility(Ability.WORKER)) {
                spawnActor(location);
                return true;
            }
        }
        return false;
    }

    /**
     * Places a new actor on an unoccupied adjacent tile.
     * Concrete subclasses define which actor to create.
     *
     * @param location the location of this tree
     */
    protected abstract void spawnActor(Location location);

    /**
     * Safely spawns one actor on the first valid adjacent tile found and applies its spawn effect.
     *
     * @param location the location of this tree
     * @param actorFactory factory that creates the actor to spawn
     * @return true if an actor was spawned successfully
     */
    protected boolean spawnActorOnAdjacentTile(Location location, Supplier<Actor> actorFactory) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Actor actor = actorFactory.get();

            if (adjacent.canActorEnter(actor)) {
                try {
                    adjacent.addActor(actor);
                    applySpawnEffect(actor, adjacent);
                    return true;
                } catch (GameEngineException ignored) {
                    // Try the next adjacent tile if this location cannot accept the actor.
                }
            }
        }

        return false;
    }

    /**
     * Applies spawn effects without depending on concrete creature classes.
     *
     * @param actor the spawned actor
     * @param location the actor's spawn location
     */
    private void applySpawnEffect(Actor actor, Location location) {
        Spawnable spawnable = actor.asCapability(Spawnable.class).orElse(null);
        if (spawnable != null) {
            spawnable.spawnEffect(location);
        }
    }
}