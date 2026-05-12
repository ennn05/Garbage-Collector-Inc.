package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;
import game.actors.Parasite;
import game.actors.Slime;
import game.interfaces.Spawnable;
import game.interfaces.Spawner;
import game.status.PoisonStatus;

import java.util.Objects;
import java.util.Random;

public class Vent extends Ground implements Spawner {
    private static final int SPAWN_POISON_DURATION = 5;
    private static final int SPAWN_POISON_DAMAGE = 1;
    private static final int SPAWN_CHECK_RADIUS = 1;

    private final Random random = new Random();

    public Vent() {
        super('v', "Vent");
    }

    @Override
    public void tick(Location location) {
        boolean spawned = false;
        for (Location nearby : location.getNearbyLocations(SPAWN_CHECK_RADIUS)) {
            if (nearby.getActorAs(ContractedWorker.class) != null) {
                try {
                    location.addActor(this.spawn(location));
                    Objects.requireNonNull(location.getActorAs(Spawnable.class)).spawnEffect(location);
                    spawned = true;
                } catch (GameEngineException | NullPointerException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
        if (spawned) {
            for (Location nearby : location.getNearbyLocations(SPAWN_CHECK_RADIUS)) {
                Actor actor = nearby.getActor();
                actor.addStatus(new PoisonStatus(SPAWN_POISON_DURATION, SPAWN_POISON_DAMAGE));
            }
        }
    }

    @Override
    public Actor spawn(Location location) {
        if (random.nextBoolean()) {
            return Slime.getSlimeSpawn();
        }
        return Parasite.getParasiteSpawn();
    }
}
