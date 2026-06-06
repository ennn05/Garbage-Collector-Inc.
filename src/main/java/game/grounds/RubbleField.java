package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Undead;

import java.util.List;
import java.util.Optional;

public class RubbleField extends CrackableGround {
    private static final int DEGRADATION_LEVEL = 2;
    private static final int MUMMIFIED_DURATION = 20;
    private Actor buriedActor = null;
    private int buryCounter = 0;

    public RubbleField() {
        super('Ʊ', "Rubble", DEGRADATION_LEVEL);
    }

    public String bury(Actor actor, GameMap currentMap) {
        this.buriedActor = actor;
        currentMap.removeActor(actor);
        return actor + " is buried under " + this;
    }

    private static int chebyshev(Location location, Location center) {
        int dx = Math.abs(location.x() - center.x());
        int dy = Math.abs(location.y() - center.y());
        return Math.max(dx, dy);
    }

    private Optional<Location> findNearestSpawnLocation(Location center, Actor actor, GameMap map) {
        int maxRadius = Math.max(map.getXRange().max(), map.getYRange().max());
        for (int r = 1; r <= maxRadius; r++) {
            List<Location> allWithinR = center.getNearbyLocations(r);
            for (Location loc : allWithinR) {
                if (chebyshev(loc, center) != r) continue;
                if (loc.containsAnActor()) continue;
                if (!loc.getGround().canActorEnter(actor)) continue;
                return Optional.of(loc);
            }
        }
        return Optional.empty();
    }

    public String mummify(Actor actor, Location location) {
        String result;
        try {
            Undead undead = Undead.getUndeadSpawn();
            Optional<Location> nearest = findNearestSpawnLocation(location, undead, location.map());
            if (nearest.isPresent()) {
                nearest.get().addActor(undead);
                undead.spawnEffect(nearest.get());
                result = actor + " reemerges as " + undead + " at " + nearest.get();
            } else {
                result = "Failed to spawn undead: no valid spawn location";
            }
        } catch (GameEngineException e) {
            result = "Failed to spawn undead: " + e.getMessage();
        }
        return result;
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public void tick(Location location) {
        if (this.buriedActor != null) {
            this.buryCounter++;
            if (this.buryCounter > MUMMIFIED_DURATION) {
                System.out.println(mummify(this.buriedActor, location));
                this.buriedActor = null;
                this.buryCounter = 0;
            }
        }
        if (!location.getItems().isEmpty()) {
            for (Item item : location.getItems()) {
                location.removeItem(item);
            }
        }
    }

    @Override
    public void onActorStep(Location location, Actor actor) {}

    @Override
    public Ground degrade() {
        return this;
    }
}
