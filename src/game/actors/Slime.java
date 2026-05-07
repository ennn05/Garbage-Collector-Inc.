package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.ConsumeGroundConsumableBehaviour;
import game.behaviours.WanderBehaviour;
import game.inventory.BasicInventory;

import java.util.Map;
import java.util.TreeMap;

/**
 * A non-hostile creature that prefers eating consumables on the ground
 * before wandering around.
 */
public class Slime extends Actor {
    private static final int HIT_POINTS = 25;

    // priority map
    private final Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    public Slime() {
        super("Slime", '⍾', HIT_POINTS, new BasicInventory());
        behaviours.put(1, new ConsumeGroundConsumableBehaviour()); // highest priority
        behaviours.put(999, new WanderBehaviour());
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        for (Behaviour<Actor, Action> behaviour : behaviours.values()) { // do behavior as priority
            Action action = behaviour.operate(this, map.locationOf(this));

            if (action != null) {
                return action;
            }
        }

        return new DoNothingAction();                                     // 如果所有 behaviour 都没有动作，就什么都不做
    }
}