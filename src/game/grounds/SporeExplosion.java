package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A volatile cluster of pressurised spores.
 *
 * When any actor steps on it:
 *   - Deals 5 damage to every actor within a 2-tile radius (AoE).
 *   - Converts all adjacent Floor tiles to BlightFungus (AoE terrain).
 *   - Converts itself back to a Floor tile (self-destructs).
 *
 * Does not spread passively.
 */
public class SporeExplosion extends FungalGround {

    private static final int EXPLOSION_DAMAGE = 5;
    private static final int EXPLOSION_RADIUS = 2;

    public SporeExplosion() {
        super('*', "SporeExplosion");
    }

    @Override
    protected void onActorPresent(Actor actor, Location location) {
        System.out.println("BOOM! A SporeExplosion detonates at " + location + "!");

        for (Location nearby : location.getNearbyLocations(EXPLOSION_RADIUS)) {
            if (nearby.containsAnActor()) {
                Actor victim = nearby.getActor();
                victim.hurt(EXPLOSION_DAMAGE);
                System.out.println(victim + " takes " + EXPLOSION_DAMAGE + " damage from the spore explosion!");
            }
        }

        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.getGround() instanceof Floor) {
                adj.setGround(new BlightFungus());
                System.out.println("BlightFungus erupts at " + adj + "!");
            }
        }

        location.setGround(new Floor());
        System.out.println("SporeExplosion collapses into Floor.");
    }

    @Override
    protected void spread(Location location) {
        // SporeExplosion does not spread passively; it only detonates on contact.
    }
}
