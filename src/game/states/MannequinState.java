package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Defines the contract for a Mannequin behavioural state.
 * Each state decides its own transitions, entry effects, and set of behaviours.
 */
public interface MannequinState {

    /**
     * Evaluate whether to transition to a new state given the current game context.
     *
     * @param mannequin the Mannequin actor
     * @param location the Mannequin's current location
     * @param manager the state manager providing access to all state instances
     * @param data mutable shared data used across state turns
     * @return the next state (may be {@code this} to stay in the current state)
     */
    MannequinState evaluate(Actor mannequin, Location location, StateManager manager, StateData data);

    /**
     * Called once when entering this state.
     * Applies transition effects (item drops, AOE damage, etc.).
     *
     * @param mannequin the Mannequin actor
     * @param location the Mannequin's current location
     * @param data mutable shared data used across state turns
     */
    void onEnter(Actor mannequin, Location location, StateData data);

    /**
     * Returns the ordered list of behaviours active in this state.
     * The Mannequin executes the first behaviour that returns a non-null action.
     *
     * @return list of behaviours for this state
     */
    List<Behaviour<Actor, Action>> getBehaviours();

}
