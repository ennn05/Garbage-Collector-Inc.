package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.behaviours.AttackWorkerBehaviour;
import game.behaviours.PursueWorkerBehaviour;
import game.behaviours.WanderBehaviour;
import game.enums.Ability;
import game.inventory.BasicInventory;
import game.world.FacilityAlarmSystem;

/**
 * A hostile moon creature that attacks nearby workers and otherwise wanders.
 */
public class Undead extends Actor {
    private static final int HIT_POINTS = 15;
    private static final int ATTACK_DAMAGE = 1;
    private static final int HIT_RATE = 10;

    private final Behaviour<Actor, Action> attackBehaviour = new AttackWorkerBehaviour();
    private final Behaviour<Actor, Action> wanderBehaviour = new WanderBehaviour();
    private final Behaviour<Actor, Action> pursueWorkerBehaviour = new PursueWorkerBehaviour();

    public Undead() {
        super("Undead", 'Ѫ', HIT_POINTS, new BasicInventory());
        this.setIntrinsicWeapon(new IntrinsicWeapon(ATTACK_DAMAGE, "punches", HIT_RATE, "fist") {});
        this.enableAbility(Ability.HOSTILE);
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Action action = attackBehaviour.operate(this, map.locationOf(this));

        if (action != null) {
            return action;
        }

        FacilityAlarmSystem alarmSystem = FacilityAlarmSystem.forMap(map); // read the alarm status of the map

        if (alarmSystem != null && alarmSystem.isAlarmActive()) {
            action = pursueWorkerBehaviour.operate(this, map.locationOf(this));
        } else {
            action = wanderBehaviour.operate(this, map.locationOf(this));
        }

        if (action != null) {
            return action;
        }

        return new DoNothingAction();
    }
}