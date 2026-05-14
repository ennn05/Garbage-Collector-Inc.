package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.DropAction;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Mannequin stands motionless, indistinguishable from real furniture.
 * Transitions to ActiveState after 10 quiet turns, or instantly to BerserkState
 * when a lone worker is spotted.
 */
public class IdleState implements MannequinState {
    private static final int WORKER_DETECTION_RADIUS = 3;
    private static final int IDLE_TURNS_FOR_ACTIVE = 10;

    @Override
    public MannequinState evaluate(Actor mannequin, Location location, StateManager manager, StateData data) {
        data.incrementIdleTurns();
        int workers = countNearbyWorkers(location);

        if (workers == 1) {
            return manager.berserk();
        }
        if (workers == 0 && data.getIdleTurnCount() >= IDLE_TURNS_FOR_ACTIVE) {
            return manager.active();
        }
        return this;
    }

    @Override
    public void onEnter(Actor mannequin, Location location, StateData data) {
        data.resetIdleTurns();
        data.setDisplayChar('M');

        for (Item item : new ArrayList<>(mannequin.getInventory().getItems())) {
            new DropAction(item).execute(mannequin, location.map());
        }
    }

    @Override
    public List<Behaviour<Actor, Action>> getBehaviours() {
        return Collections.emptyList();
    }

    @Override
    public String getStateName() {
        return "Idle";
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
}
