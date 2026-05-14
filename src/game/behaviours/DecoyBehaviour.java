package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

import java.util.ArrayList;
import java.util.List;

/**
 * While disguised, the Mannequin heals itself each turn when adjacent to a worker.
 * Nearby workers unknowingly reinforce the Mannequin by allowing it to regenerate HP.
 */
public class DecoyBehaviour implements Behaviour<Actor, Action> {
    private static final int HEAL_AMOUNT = 2;

    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (!dest.containsAnActor()) {
                continue;
            }
            Actor worker = dest.getActor();
            if (!worker.hasAbility(Ability.WORKER)) {
                continue;
            }

            return new DecoyAction(worker);
        }

        return null;
    }

    /**
     * Heals the Mannequin and forces an adjacent worker to drop one item.
     */
    private class DecoyAction extends Action {
        private final Actor target;

        DecoyAction(Actor target) {
            this.target = target;
        }

        @Override
        public String execute(Actor actor, edu.monash.fit2099.engine.positions.GameMap map) {
            actor.heal(HEAL_AMOUNT);

            List<Item> items = new ArrayList<>(target.getInventory().getItems());
            if (!items.isEmpty()) {
                Item dropped = items.get(0);
                target.getInventory().remove(dropped);
                map.locationOf(target).addItem(dropped);
                return actor + " heals " + HEAL_AMOUNT + " HP and causes " + target + " to drop " + dropped;
            }

            return actor + " heals " + HEAL_AMOUNT + " HP while lurking near " + target;
        }

        @Override
        public String menuDescription(Actor actor) {
            return actor + " uses decoy near " + target;
        }
    }
}
