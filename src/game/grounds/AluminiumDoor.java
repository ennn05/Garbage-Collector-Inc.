package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.AccessLevel;
import game.interfaces.Cuttable;
import game.items.AluminiumScrap;

import java.util.Random;

/**
 * An aluminium door that requires Access Level 1 or higher to open.
 * When unlocked, it delivers a faulty electrical shock dealing 2 damage to the actor.
 * Can be cut with a plasma cutter.
 */
public class AluminiumDoor extends Door implements Cuttable {
    private static final int SHOCK_DAMAGE = 2;
    private static final double EXPLOSION_CHANCE = 0.25;
    private static final int EXPLOSION_DAMAGE = 100;

    private final Random random = new Random();

    /**
     * Constructor for AluminiumDoor.
     */
    public AluminiumDoor() {
        super('=', "Aluminium Door");
    }

    /**
     * Get the required access level for this door.
     *
     * @return AccessLevel.LEVEL_1
     */
    @Override
    public AccessLevel getRequiredClearance() {
        return AccessLevel.LEVEL_1;
    }

    /**
     * When unlocked, deal exactly 2 damage to the actor from electrical shock.
     *
     * @param actor the actor unlocking this object
     * @param location the location of this unlockable object
     * @return result description
     */
    @Override
    public String onUnlocked(Actor actor, Location location) {
        actor.hurt(SHOCK_DAMAGE);
        return actor + " is shocked by the Aluminium Door for " + SHOCK_DAMAGE + " damage.";
    }

    /**
     * Check if this door can be cut.
     *
     * @param actor the actor attempting to cut
     * @return true (doors can always be cut)
     */
    @Override
    public boolean canBeCut(Actor actor) {
        return true;
    }

    /**
     * Handle cutting the aluminium door.
     * 25% chance of explosion dealing 100 damage to adjacent actors.
     *
     * @param actor the actor performing the cut
     * @param location the location of this door
     * @return result description of the cutting
     */
    @Override
    public String onCut(Actor actor, Location location) {
        String result = actor + " cuts through the Aluminium Door with the Plasma Cutter! " +
                "It transforms into a Floor tile.";

        // Check for explosion
        if (random.nextDouble() < EXPLOSION_CHANCE) {
            result += "\nBOOM! The door explodes!";
            
            // Damage all adjacent actors
            for (Exit exit : location.getExits()) {
                Location adjacent = exit.getDestination();
                if (adjacent.containsAnActor()) {
                    Actor adjacentActor = adjacent.getActor();
                    adjacentActor.hurt(EXPLOSION_DAMAGE);
                    result += "\n" + adjacentActor + " is hit by shrapnel for " + EXPLOSION_DAMAGE + " damage!";
                }
            }
        }

        System.out.println(result);
        return result;
    }

    /**
     * Get the item dropped when this door is cut.
     *
     * @return AluminiumScrap
     */
    @Override
    public Item getDroppedItem() {
        return new AluminiumScrap();
    }
}