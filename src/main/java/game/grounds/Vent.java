package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Parasite;
import game.actors.ScrapSnatcher;
import game.actors.Slime;
import game.actors.Undead;
import game.enums.Ability;
import game.interfaces.Cuttable;
import game.interfaces.Spawnable;
import game.interfaces.Spawner;
import game.items.IndustrialFan;
import game.status.PoisonStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A ground tile that spawns a hostile creature when a contracted worker is nearby.
 * <p>
 * When activated, the vent creates a random spawnable actor, places it on the
 * current location, triggers its spawn effect, and poisons nearby actors. It can
 * be cut with a plasma cutter.
 */
public class Vent extends Ground implements Spawner, Cuttable {
    private static final int SPAWN_POISON_DURATION = 5;
    private static final int SPAWN_POISON_DAMAGE = 1;
    private static final int SPAWN_CHECK_RADIUS = 1;

    private final Random random = new Random();

    /**
     * Creates a vent ground tile.
     */
    public Vent() {
        super('V', "Vent");
    }

    /**
     * Spawns a hostile creature when a contracted worker is nearby.
     *
     * @param location the location containing this vent
     */
    @Override
    public void tick(Location location) {
        if (!hasWorkerNearby(location) || location.containsAnActor()) {
            return;
        }

        Actor spawnedActor = spawn(location);
        try {
            location.addActor(spawnedActor);
            applySpawnEffect(spawnedActor, location);
            poisonNearbyActors(location);
        } catch (GameEngineException ignored) {
            // The spawn attempt is safely ignored if the engine rejects the actor placement.
        }
    }

    /**
     * Creates a random spawnable actor for this vent.
     *
     * @param location the location where the actor will be spawned
     * @return a new hostile creature
     */
    @Override
    public Actor spawn(Location location) {
        List<Supplier<Actor>> spawnOptions = new ArrayList<>();
        spawnOptions.add(Slime::getSlimeSpawn);
        spawnOptions.add(Parasite::getParasiteSpawn);
        spawnOptions.add(ScrapSnatcher::getScrapSnatcherSpawn);

        return spawnOptions.get(random.nextInt(spawnOptions.size())).get();
    }

    /**
     * Checks whether a worker is nearby.
     *
     * @param location the location containing this vent
     * @return true if a worker is within the check radius
     */
    private boolean hasWorkerNearby(Location location) {
        for (Location nearby : location.getNearbyLocations(SPAWN_CHECK_RADIUS)) {
            if (nearby.containsAnActor() && nearby.getActor().hasAbility(Ability.WORKER)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Applies the spawn side effect if the spawned actor supports it.
     *
     * @param spawnedActor the actor that has just been spawned
     * @param location the spawn location
     */
    private void applySpawnEffect(Actor spawnedActor, Location location) {
        Spawnable spawnable = spawnedActor.asCapability(Spawnable.class).orElse(null);
        if (spawnable != null) {
            spawnable.spawnEffect(location);
        }
    }

    /**
     * Poisons all nearby actors after the vent spawns an actor.
     *
     * @param location the location containing this vent
     */
    private void poisonNearbyActors(Location location) {
        for (Location nearby : location.getNearbyLocations(SPAWN_CHECK_RADIUS)) {
            if (nearby.containsAnActor()) {
                Actor actor = nearby.getActor();
                actor.addStatus(new PoisonStatus(SPAWN_POISON_DURATION, SPAWN_POISON_DAMAGE));
            }
        }
    }

    /**
     * Checks if this vent can be cut.
     *
     * @param actor the actor attempting to cut
     * @return true because vents can always be cut
     */
    @Override
    public boolean canBeCut(Actor actor) {
        return true;
    }

    /**
     * Handles cutting the vent. The vent spawns an Undead on the current
     * location and then transforms into a Floor tile.
     *
     * @param actor the actor performing the cut
     * @param location the location of this vent
     * @return result description of the cutting
     */
    @Override
    public String onCut(Actor actor, Location location) {
        String result = actor + " cuts through the Vent with the Plasma Cutter! "
                + "It transforms into a Floor tile.";

        try {
            Actor undead = Undead.getUndeadSpawn();
            location.addActor(undead);
            applySpawnEffect(undead, location);
            result += "\nAn Undead instantly spawns from the vent!";
        } catch (GameEngineException ignored) {
            result += "\nFailed to spawn Undead from the vent.";
        }

        return result;
    }

    /**
     * Gets the item dropped when this vent is cut.
     *
     * @return IndustrialFan
     */
    @Override
    public Item getDroppedItem() {
        return new IndustrialFan();
    }
}