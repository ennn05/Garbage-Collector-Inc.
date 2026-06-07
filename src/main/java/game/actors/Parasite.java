package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.InfectBehaviour;
import game.behaviours.WanderBehaviour;
import game.enums.Ability;
import game.interfaces.Spawnable;
import game.interfaces.Spawner;
import game.inventory.BasicInventory;

/**
 * A hostile creature that can infect nearby targets and spawn copies of itself.
 */
public class Parasite extends Creature implements Spawner, Spawnable {
    private static final int HIT_POINTS = 30;
    private static final int SPAWN_DAMAGE = 2;
    private static final int SPAWN_EFFECT_RADIUS = 1;

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
    public static Actor getParasiteSpawn() {
        return new Parasite();
    }

    /**
     * Spawns a fresh parasite instance.
     *
     * @param location the location where the actor will be spawned
     * @return a new parasite
     */
    @Override
    public Actor spawn(Location location) {
        return getParasiteSpawn();
    }

    /**
     * Parasite hurts adjacent workers on spawn.
     *
     * @param location the location of the entity that is spawned on
     */
    @Override
    public void spawnEffect(Location location) {
        for (Location nearby : location.getNearbyLocations(SPAWN_EFFECT_RADIUS)) {
            if (nearby.containsAnActor() && nearby.getActor().hasAbility(Ability.WORKER)) {
                nearby.getActor().hurt(SPAWN_DAMAGE);
            }
        }
    }
}