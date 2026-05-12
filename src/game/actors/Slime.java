package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.DropAction;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.ConsumeGroundConsumableBehaviour;
import game.behaviours.WanderBehaviour;
import game.interfaces.Spawnable;
import game.inventory.BasicInventory;

import java.util.ArrayList;

/**
 * A non-hostile creature that prefers eating consumables on the ground
 * before wandering around.
 */
public class Slime extends Creature implements Spawnable {
    private static final int HIT_POINTS = 25;
    private static final int SPAWN_EFFECT_RADIUS = 1;

    public Slime() {
        super("Slime", '⍾', HIT_POINTS, new BasicInventory());
        addBehaviour(1, new ConsumeGroundConsumableBehaviour());
        addBehaviour(999, new WanderBehaviour());
    }

    public static Actor getSlimeSpawn() {
        return new Slime();
    }

    @Override
    public void spawnEffect(Location location) {
        for (Location nearby : location.getNearbyLocations(SPAWN_EFFECT_RADIUS)) {
            Actor worker = nearby.getActorAs(ContractedWorker.class);
            if (worker != null) {
                for (Item item : new ArrayList<>(worker.getInventory().getItems())) {
                    new DropAction(item).execute(worker, location.map());
                }
            }
        }
    }
}