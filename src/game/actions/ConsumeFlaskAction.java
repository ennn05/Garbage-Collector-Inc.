package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.Flask;

/**
 * An action representing the desperate, mid-combat decision to chug whatever
 * liquid is sloshing around inside a flask.
 * Because nothing cures catastrophic injuries quite like aggressive hydration.
 *
 * @see Action
 */
public class ConsumeFlaskAction extends Action {

    /**
     * When executed, it will check the actor's inventory for a Flask.
     * If a Flask is found and still has uses remaining, it will consume one use
     * and heal the actor by 1 point.
     *
     * @param actor The actor consuming the flask.
     * @param map The map the actor is on.
     * @return the description of the result of the action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Inventory inventory = actor.getInventory();

        for (Item item : inventory.getItems()) {
            if (item instanceof Flask flask) {
                if (flask.hasUsesRemaining()) {
                    flask.consume(actor);
                    return actor + " drinks flask, which heals them by 1 point of health. Remaining uses: "
                            + flask.getRemainingUses();
                }
                return actor + " tries to drink from an empty flask.";
            }
        }

        return actor + " does not carry a flask.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " consumes flask.";
    }
}