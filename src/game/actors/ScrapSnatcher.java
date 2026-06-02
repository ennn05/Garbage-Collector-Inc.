package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.behaviours.AttackWorkerBehaviour;
import game.behaviours.SnatchDepositableResourceBehaviour;
import game.enums.Ability;
import game.interfaces.Infectable;
import game.interfaces.Spawnable;
import game.inventory.BasicInventory;
import game.status.InfectedSnatcherStatus;
import game.utility.LootExplosion;

/**
 * A creature that randomly moves around the map and hoards depositable resources.
 * <p>
 * When infected by a Parasite, it stops snatching resources, attacks adjacent
 * workers with its bare fist, and loses one hit point every turn until it dies.
 */
public class ScrapSnatcher extends Creature implements Spawnable, Infectable {
    private static final int HIT_POINTS = 25;
    private static final int ATTACK_DAMAGE = 1;
    private static final int HIT_RATE = 10;

    private final Behaviour<Actor, Action> snatchBehaviour;
    private final Behaviour<Actor, Action> infectedAttackBehaviour;
    private final LootExplosion lootExplosion;

    /**
     * Creates a new Scrap Snatcher with its normal and infected behaviours.
     */
    public ScrapSnatcher() {
        super("Scrap Snatcher", 's', HIT_POINTS, new BasicInventory());
        this.setIntrinsicWeapon(new IntrinsicWeapon(ATTACK_DAMAGE, "punches", HIT_RATE, "bare fist") {});
        this.enableAbility(Ability.HOSTILE);
        this.snatchBehaviour = new SnatchDepositableResourceBehaviour();
        this.infectedAttackBehaviour = new AttackWorkerBehaviour();
        this.lootExplosion = new LootExplosion();
    }

    /**
     * Creates a fresh Scrap Snatcher instance.
     *
     * @return a new Scrap Snatcher
     */
    public static Actor getScrapSnatcherSpawn() {
        return new ScrapSnatcher();
    }

    /**
     * Selects behaviour based on whether the snatcher is infected.
     *
     * @param actions collection of possible actions for this actor
     * @param lastAction the action this actor took last turn
     * @param map the map containing this actor
     * @param display the display object
     * @return the chosen action
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location location = map.locationOf(this);
        Action action;

        if (this.hasStatus(InfectedSnatcherStatus.class)) {
            action = infectedAttackBehaviour.operate(this, location);
        } else {
            action = snatchBehaviour.operate(this, location);
        }

        if (action != null) {
            return action;
        }

        return new DoNothingAction();
    }

    /**
     * Applies the infection effect from a Parasite.
     *
     * @param otherActor the actor infecting this snatcher
     * @param gameMap the game map containing this snatcher
     * @return a message describing the infection result
     */
    @Override
    public String infect(Actor otherActor, GameMap gameMap) {
        if (!this.hasStatus(InfectedSnatcherStatus.class)) {
            this.addStatus(new InfectedSnatcherStatus(this));
        }

        return otherActor + " infects " + this
                + ". It stops snatching resources and becomes rabid.";
    }

    /**
     * Triggers a loot explosion when this snatcher is spawned.
     *
     * @param location the location where this snatcher is spawned
     */
    @Override
    public void spawnEffect(Location location) {
        lootExplosion.explode(location);
    }
}