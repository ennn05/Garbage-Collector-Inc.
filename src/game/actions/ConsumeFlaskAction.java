package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Drinkable;

/**
 * An action for drinking from a drinkable item.
 */
public class ConsumeFlaskAction extends Action {
    private final Drinkable drinkable;

    /**
     * Constructor.
     *
     * @param drinkable the drinkable item to drink from
     */
    public ConsumeFlaskAction(Drinkable drinkable) {
        this.drinkable = drinkable;
    }

    /**
     * Drink from the selected drinkable item.
     *
     * @param actor The actor drinking from the item.
     * @param map The map the actor is on.
     * @return the description of the result of the action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return drinkable.drink(actor);
    }

    /**
     * Menu description.
     *
     * @param actor The actor performing the action.
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " drinks from " + drinkable;
    }
}