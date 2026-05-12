package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.behaviours.AttackWorkerBehaviour;
import game.behaviours.PursueWorkerBehaviour;
import game.behaviours.WanderBehaviour;
import game.enums.Ability;
import game.interfaces.Infectable;
import game.inventory.BasicInventory;

/**
 * A hostile moon creature that attacks nearby workers, pursues workers during
 * facility alarm, and otherwise wanders.
 */
public class Undead extends Creature implements Infectable {
    private static final int HIT_POINTS = 15;
    private static final int ATTACK_DAMAGE = 1;
    private static final int HIT_RATE = 10;

    public Undead() {
        super("Undead", 'Ѫ', HIT_POINTS, new BasicInventory());
        this.setIntrinsicWeapon(new IntrinsicWeapon(ATTACK_DAMAGE, "punches", HIT_RATE, "fist") {});
        this.enableAbility(Ability.HOSTILE);

        addBehaviour(1, new AttackWorkerBehaviour());
        addBehaviour(2, new PursueWorkerBehaviour());
        addBehaviour(999, new WanderBehaviour());
    }

    @Override
    public String infect(Actor otherActor, GameMap gameMap) {
        return otherActor + " infected " + this + "\n" + this.unconscious(gameMap);
    }
}
