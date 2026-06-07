package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.enums.Ability;
import game.inventory.BasicInventory;
import game.states.MannequinState;
import game.states.StateData;
import game.states.StateManager;

/**
 * A stateful creature that cycles through Idle, Active, Berserk, and Mimic states
 * depending on worker proximity, inventory content, and health.
 */
public class Mannequin extends Creature {
    private static final int HIT_POINTS = 30;
    private static final int ATTACK_DAMAGE = 5;
    private static final int HIT_RATE = 75;

    private final StateManager manager;
    private final StateData data;
    private MannequinState currentState;

    public Mannequin() {
        super("Mannequin", 'Ω', HIT_POINTS, new BasicInventory());
        this.setIntrinsicWeapon(new IntrinsicWeapon(ATTACK_DAMAGE, "slashes", HIT_RATE, "claws") {});
        this.enableAbility(Ability.HOSTILE);

        manager = new StateManager();
        data = new StateData();
        currentState = manager.idle();
    }

    /**
     * Returns the state-driven display character.
     * Defaults to 'M'; changes to a worker character while in MimicState.
     */
    @Override
    public char getDisplayChar() {
        return data.getDisplayChar();
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        Location location = map.locationOf(this);

        MannequinState nextState = currentState.evaluate(this, location, manager, data);
        if (nextState != currentState) {
            currentState = nextState;
            currentState.onEnter(this, location, data);
        }

        for (Behaviour<Actor, Action> behaviour : currentState.getBehaviours()) {
            Action action = behaviour.operate(this, location);
            if (action != null) {
                return action;
            }
        }

        return new DoNothingAction();
    }
}
