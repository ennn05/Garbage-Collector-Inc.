package game.actors;

import game.behaviours.ConsumeGroundConsumableBehaviour;
import game.behaviours.WanderBehaviour;
import game.inventory.BasicInventory;

/**
 * A non-hostile creature that prefers eating consumables on the ground
 * before wandering around.
 */
public class Slime extends Creature {
    private static final int HIT_POINTS = 25;

    public Slime() {
        super("Slime", '⍾', HIT_POINTS, new BasicInventory());
        addBehaviour(1, new ConsumeGroundConsumableBehaviour());
        addBehaviour(999, new WanderBehaviour());
    }
}