package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.FirstAidKit;

/**
 * Action for using a first aid kit.
 */
public class UseFirstAidKitAction extends Action {
    private final FirstAidKit firstAidKit;

    public UseFirstAidKitAction(FirstAidKit firstAidKit) {
        this.firstAidKit = firstAidKit;
    }

    /**
     * Use the first aid kit if it is ready.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return the result description
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!firstAidKit.canUse()) {
            return actor + " cannot use the First Aid Kit for another "
                    + firstAidKit.getCooldownTurnsRemaining() + " turns.";
        }

        firstAidKit.use(actor);
        return actor + " uses the First Aid Kit and fully restores health while increasing maximum health by 1.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses the First Aid Kit";
    }
}