package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Heals the Mannequin and forces an adjacent worker to drop one item.
 */
public class DecoyAction extends Action {
    private final Actor target;
    private final int healAmount;

    public DecoyAction(Actor target, int healAmount) {
        this.target = target;
        this.healAmount = healAmount;
    }

    @Override
    public String execute(Actor actor, edu.monash.fit2099.engine.positions.GameMap map) {
        actor.heal(healAmount);

        List<Item> items = new ArrayList<>(target.getInventory().getItems());
        if (!items.isEmpty()) {
            Item dropped = items.getFirst();
            target.getInventory().remove(dropped);
            map.locationOf(target).addItem(dropped);
            return actor + " heals " + healAmount + " HP and causes " + target + " to drop " + dropped;
        }

        return actor + " heals " + healAmount + " HP while lurking near " + target;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses decoy near " + target;
    }
}