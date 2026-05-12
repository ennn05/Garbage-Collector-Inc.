package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import game.behaviours.InfectBehaviour;
import game.behaviours.WanderBehaviour;
import game.interfaces.Spawnable;
import game.inventory.BasicInventory;

/**
 * A hostile creature that can infect nearby targets and spawn copies of itself.
 */
public class Parasite extends Creature implements Spawnable {
    private static final int HIT_POINTS = 30;

    /**
     * Creates a new parasite with its default stats, inventory, and behaviours.
     */
    public Parasite() {
        super("Parasite", 'x', HIT_POINTS, new BasicInventory());
        addBehaviour(1, new InfectBehaviour());
        addBehaviour(999, new WanderBehaviour());
    }

    /**
     * Spawns a fresh parasite instance.
     *
     * @return a new parasite
     */
    @Override
    public Actor spawn() {
        return new Parasite();
    }
}
