package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.DecoyBehaviour;
import game.enums.Ability;

import java.util.ArrayList;
import java.util.List;

/**
 * The Mannequin disguises itself as an item from its inventory, pushing nearby workers
 * away on entry and regenerating HP each turn via DecoyBehaviour.
 * Snaps back to BerserkState the moment a lone worker is in range.
 */
public class MimicState implements MannequinState {
    private static final int WORKER_DETECTION_RADIUS = 3;
    private static final int DISGUISE_DURATION = 5;
    private static final char DEFAULT_DISGUISE_CHAR = '?';

    private final List<Behaviour<Actor, Action>> behaviours;

    public MimicState() {
        behaviours = new ArrayList<>();
        behaviours.add(new DecoyBehaviour());
    }

    @Override
    public MannequinState evaluate(Actor mannequin, Location location, StateManager manager, StateData data) {
        data.decrementDisguiseTurns();

        int workers = countNearbyWorkers(location);

        if (workers == 1) {
            return manager.berserk();
        }

        if (data.getDisguiseTurnsLeft() == 0) {
            if (mannequin.getInventory().getItems().isEmpty()) {
                return manager.idle();
            }

            return manager.active();
        }

        return this;
    }

    @Override
    public void onEnter(Actor mannequin, Location location, StateData data) {
        data.setDisguiseTurnsLeft(DISGUISE_DURATION);

        List<Item> items = new ArrayList<>(mannequin.getInventory().getItems());
        if (!items.isEmpty()) {
            Item disguiseItem = items.get(0);
            data.setDisplayChar(disguiseItem.getDisplayChar());
            mannequin.getInventory().remove(disguiseItem);
        } else {
            data.setDisplayChar(DEFAULT_DISGUISE_CHAR);
        }

        for (Exit exit : location.getExits()) {
            Location workerLoc = exit.getDestination();

            if (!workerLoc.containsAnActor()) {
                continue;
            }

            Actor worker = workerLoc.getActor();

            if (!worker.hasAbility(Ability.WORKER)) {
                continue;
            }

            int currentDist = chebyshev(location, workerLoc);

            for (Exit pushExit : workerLoc.getExits()) {
                Location pushDest = pushExit.getDestination();

                if (chebyshev(location, pushDest) > currentDist && pushDest.canActorEnter(worker)) {
                    location.map().moveActor(worker, pushDest);
                    break;
                }
            }
        }
    }

    @Override
    public List<Behaviour<Actor, Action>> getBehaviours() {
        return behaviours;
    }

    private int countNearbyWorkers(Location location) {
        int count = 0;

        for (Location nearby : location.getNearbyLocations(WORKER_DETECTION_RADIUS)) {
            if (nearby.containsAnActor() && nearby.getActor().hasAbility(Ability.WORKER)) {
                count++;
            }
        }

        return count;
    }

    private int chebyshev(Location a, Location b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }
}