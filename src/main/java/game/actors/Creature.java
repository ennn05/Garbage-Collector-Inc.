package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.Map;
import java.util.TreeMap;

/**
 * An abstract actor that automatically chooses actions from a priority-ordered
 * set of behaviours.
 */
public abstract class Creature extends Actor {
    private final Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    /**
     * Constructor.
     *
     * @param name the creature's name
     * @param displayChar the character used to display the creature
     * @param hitPoints the creature's hit points
     * @param inventory the creature's inventory
     */
    public Creature(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
    }

    /**
     * Add a behaviour with a priority.
     * Lower priority numbers are executed first.
     *
     * @param priority the behaviour priority
     * @param behaviour the behaviour to add
     */
    protected void addBehaviour(int priority, Behaviour<Actor, Action> behaviour) {
        behaviours.put(priority, behaviour);
    }

    /**
     * Automatically choose the first available action from the behaviour list.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the action selected by this creature
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        for (Behaviour<Actor, Action> behaviour : behaviours.values()) {
            Action action = behaviour.operate(this, map.locationOf(this));

            if (action != null) {
                return action;
            }
        }

        return new DoNothingAction();
    }
}