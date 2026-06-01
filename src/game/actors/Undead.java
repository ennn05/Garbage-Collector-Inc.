package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import edu.monash.fit2099.engine.positions.Exit;
import game.behaviours.AttackWorkerBehaviour;
import game.behaviours.PursueWorkerBehaviour;
import game.behaviours.WanderBehaviour;
import game.enums.Ability;
import game.grounds.BlightFungus;
import game.grounds.Floor;
import game.grounds.FungalGround;
import game.interfaces.Infectable;
import game.interfaces.Spawnable;
import game.interfaces.SporeEmitter;
import game.inventory.BasicInventory;

/**
 * A hostile moon creature that attacks nearby workers, pursues workers during
 * facility alarm, and otherwise wanders.
 */
public class Undead extends Creature implements Infectable, Spawnable, SporeEmitter {
    private static final int HIT_POINTS = 15;
    private static final int ATTACK_DAMAGE = 1;
    private static final int HIT_RATE = 10;
    private static final int SPAWN_EFFECT_RADIUS = 1;

    public Undead() {
        super("Undead", 'Ѫ', HIT_POINTS, new BasicInventory());
        this.setIntrinsicWeapon(new IntrinsicWeapon(ATTACK_DAMAGE, "punches", HIT_RATE, "fist") {});
        this.enableAbility(Ability.HOSTILE);

        addBehaviour(1, new AttackWorkerBehaviour());
        addBehaviour(2, new PursueWorkerBehaviour());
        addBehaviour(999, new WanderBehaviour());
    }

    /**
     * Spawns a fresh Undead instance.
     *
     * @return a new Undead
     */
    public static Actor getUndeadSpawn() {
        return new Undead();
    }

    @Override
    public String infect(Actor otherActor, GameMap gameMap) {
        return otherActor + " infected " + this + "\n" + this.unconscious(gameMap);
    }

    /**
     * Undead will increase its max health based on the number of creatures adjacent to it.
     *
     * @param location the location of the entity that is spawned on
     */
    @Override
    public void spawnEffect(Location location) {
        int nearbyCreatures = 0;
        for (Location nearby : location.getNearbyLocations(SPAWN_EFFECT_RADIUS)) {
            Actor creature = nearby.getActorAs(Creature.class);
            if (creature != null) {
                nearbyCreatures++;
            }
        }
        this.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, nearbyCreatures);
        this.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.INCREASE, nearbyCreatures);
    }

    /**
     * Converts ALL adjacent Floor tiles to BlightFungus (AoE spread).
     * Triggered by FungalGround.tick() each turn this Undead stands on any FungalGround tile.
     *
     * @param source the Undead's current location
     */
    @Override
    public void emitSpores(Location source) {
        int spreadCount = 0;
        for (Exit exit : source.getExits()) {
            Location adj = exit.getDestination();
            if (adj.getGround() instanceof Floor) {
                adj.setGround(new BlightFungus());
                spreadCount++;
            }
        }
        if (spreadCount > 0) {
            System.out.println(this + " triggers AoE blight spread to " + spreadCount + " adjacent tile(s)!");
        }
    }

    @Override
    public Class<? extends FungalGround> getSporeType() {
        return BlightFungus.class;
    }
}
