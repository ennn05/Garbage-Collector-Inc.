package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.interfaces.Consumable;

/**
 * Abstract base class for consumable items that can be carried in inventory.
 */
public abstract class ConsumableItem extends Item implements Consumable {

    public ConsumableItem(String name, char displayChar) {
        super(name, displayChar);
        this.makePortable();
    }

    /**
     * Provide a consume action while this item is in the owner's inventory.
     *
     * @param owner the actor carrying the item
     * @param map the map where the actor is
     * @return allowable actions for this item while being carried
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        if (canConsume(owner)) {
            actions.add(new ConsumeAction(this));
        }

        return actions;
    }
}