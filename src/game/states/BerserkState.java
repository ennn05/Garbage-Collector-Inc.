package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.DropAction;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.AttackWorkerBehaviour;
import game.behaviours.ChaseSoloWorkerBehaviour;
import game.enums.Ability;

import java.util.ArrayList;
import java.util.List;

/**
 * The Mannequin abandons subtlety and hunts workers relentlessly.
 * Entering this state triggers an immediate panic effect that forces adjacent workers
 * to drop their carried items.
 */
public class BerserkState implements MannequinState {
    private static final int WORKER_DETECTION_RADIUS = 3;
    private static final double LOW_HEALTH_THRESHOLD = 0.30;

    private final List<Behaviour<Actor, Action>> behaviours;

    public BerserkState() {
        behaviours = new ArrayList<>();
        behaviours.add(new AttackWorkerBehaviour());
        behaviours.add(new ChaseSoloWorkerBehaviour());
    }

    @Override
    public MannequinState evaluate(Actor mannequin, Location location, StateManager manager, StateData data) {
        int workers = countNearbyWorkers(location);
        boolean hasItems = !mannequin.getInventory().getItems().isEmpty();

        if (workers > 1) {
            return manager.idle();
        }

        if (workers == 0) {
            int currentHp = mannequin.getStatistic(ActorStatistics.HEALTH);
            int maxHp = mannequin.getMaximumStatistic(ActorStatistics.HEALTH);
            boolean lowHealth = currentHp <= (int) (maxHp * LOW_HEALTH_THRESHOLD);

            if (lowHealth && hasItems) {
                return manager.mimic();
            }

            return manager.active();
        }

        return this;
    }

    @Override
    public void onEnter(Actor mannequin, Location location, StateData data) {
        data.setDisplayChar('Ω');
        data.resetIdleTurns();

        for (Exit exit : location.getExits()) {
            Location nearby = exit.getDestination();

            if (!nearby.containsAnActor()) {
                continue;
            }

            Actor nearbyActor = nearby.getActor();

            if (!nearbyActor.hasAbility(Ability.WORKER)) {
                continue;
            }

            for (Item item : new ArrayList<>(nearbyActor.getInventory().getItems())) {
                new DropAction(item).execute(nearbyActor, location.map());
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
}