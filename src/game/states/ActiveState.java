package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.CollectGroundItemBehaviour;
import game.enums.Ability;

import java.util.ArrayList;
import java.util.List;

/**
 * The Mannequin roams to collect items.
 * Transitions to BerserkState on a lone worker, MimicState when full or outnumbered
 * with loot, and back to IdleState when outnumbered with nothing to show for it.
 */
public class ActiveState implements MannequinState {
    private static final int WORKER_DETECTION_RADIUS = 3;
    private static final int MAX_INVENTORY_SIZE = 3;

    private final List<Behaviour<Actor, Action>> behaviours;

    public ActiveState() {
        behaviours = new ArrayList<>();
        behaviours.add(new CollectGroundItemBehaviour());
    }

    @Override
    public MannequinState evaluate(Actor mannequin, Location location, StateManager manager, StateData data) {
        int workers = countNearbyWorkers(location);
        boolean hasItems = !mannequin.getInventory().getItems().isEmpty();
        boolean inventoryFull = mannequin.getInventory().getItems().size() >= MAX_INVENTORY_SIZE;

        if (workers == 1) {
            return manager.berserk();
        }
        if (inventoryFull || (workers > 1 && hasItems)) {
            return manager.mimic();
        }
        if (workers > 1 && !hasItems) {
            return manager.idle();
        }
        return this;
    }

    @Override
    public void onEnter(Actor mannequin, Location location, StateData data) {
        data.setDisplayChar('M');
        data.resetIdleTurns();

        List<Item> groundItems = new ArrayList<>(location.getItems());
        for (Item item : groundItems) {
            if (mannequin.getInventory().getItems().size() >= MAX_INVENTORY_SIZE) {
                break;
            }
            mannequin.getInventory().add(item);
            location.removeItem(item);
        }
    }

    @Override
    public List<Behaviour<Actor, Action>> getBehaviours() {
        return behaviours;
    }

    @Override
    public String getStateName() {
        return "Active";
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
