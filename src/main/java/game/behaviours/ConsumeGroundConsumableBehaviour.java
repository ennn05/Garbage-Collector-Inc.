package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeGroundItemAction;
import game.interfaces.Consumable;

/**
 * A behaviour that makes an actor consume one consumable item on its current tile.
 */
public class ConsumeGroundConsumableBehaviour implements Behaviour<Actor, Action> {

    @Override
    public Action operate(Actor actor, Location location) {
        for (Item item : location.getItems()) { // check the location now
            Consumable consumable = item.asCapability(Consumable.class).orElse(null);

            if (consumable != null && consumable.canConsumeFromGround(actor)) {
                return new ConsumeGroundItemAction(item, consumable); // directly eat in this location
            }
        }

        for (Exit exit : location.getExits()) { // check location beside
            Location destination = exit.getDestination();

            if (!destination.canActorEnter(actor)) {
                continue; // skip if cannot enter
            }

            for (Item item : destination.getItems()) {
                Consumable consumable = item.asCapability(Consumable.class).orElse(null);

                if (consumable != null && consumable.canConsumeFromGround(actor)) {
                    return new ConsumeGroundItemAction(destination, exit.getName(), item, consumable); // go there and eat
                }
            }
        }

        return null;
    }
}