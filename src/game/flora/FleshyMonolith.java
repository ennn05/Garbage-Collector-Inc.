package game.flora;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.WarpAction;
import game.enums.Ability;

/**
 * Final 99-Deprecated fleshy tree stage.
 * <p>
 * The monolith no longer grows or spawns creatures. Instead, when a worker is
 * standing on one of its adjacent tiles, it forcibly warps that worker to a
 * random valid location on the same map.
 */
public class FleshyMonolith extends Tree {
    private final Display display = new Display();

    /**
     * Creates a fleshy monolith displayed as {@code H}.
     */
    public FleshyMonolith() {
        super('H', "Fleshy Monolith");
    }

    /**
     * Warps the first adjacent worker found to a random valid location.
     *
     * @param location the location of this monolith
     * @return true if a worker was warped this turn
     */
    @Override
    protected boolean proximityEffect(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();

            if (adjacent.containsAnActor() && adjacent.getActor().hasAbility(Ability.WORKER)) {
                Actor worker = adjacent.getActor();
                display.println(new WarpAction().execute(worker, location.map()));
                return true;
            }
        }

        return false;
    }
}