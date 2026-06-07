package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.enums.Ability;
import game.interfaces.Consumable;
import game.interfaces.PuddleGround;
import game.interfaces.Sterilisable;
import game.status.PoisonStatus;

/**
 * A small, stationary body of mysterious liquid on the ground.
 * In a standard video game, this would just be water. On a deprecated moon
 * in the Eclipse Nebula, it could be anything from spilled engine coolant to
 * highly corrosive alien saliva.
 */
public class Puddle extends Ground implements Sterilisable, Consumable, PuddleGround {
    private static final int HEAL_AMOUNT = 1;
    private static final int POISON_TURNS = 3;

    private boolean sterilised = false;

    /**
     * Creates a Puddle tile (display character: {@code ~}).
     */
    public Puddle() {
        super('~', "Puddle");
    }

    /**
     * Puddles can always be consumed when the consume action is offered.
     * The standing-on-top requirement is enforced in allowableActions().
     *
     * @param actor the actor attempting to consume it
     * @return true
     */
    @Override
    public boolean canConsume(Actor actor) {
        return true;
    }

    /**
     * Drink directly from the puddle.
     * If the actor has sterilising ability, heal 1 HP.
     * Otherwise, apply poison for 3 turns.
     *
     * @param actor the actor consuming it
     * @return result description
     */
    @Override
    public String consume(Actor actor) {
        if (this.sterilised || actor.hasAbility(Ability.STERILISING)) {
            actor.heal(HEAL_AMOUNT);
            return actor + " drinks from the puddle safely and heals " + HEAL_AMOUNT + " health point.";
        }

        actor.addStatus(new PoisonStatus(POISON_TURNS));
        return actor + " drinks from the puddle and is poisoned for " + POISON_TURNS + " turns.";
    }

    /**
     * Returns the menu label shown when the player is offered the drink action.
     *
     * @param actor the actor that would drink
     * @return a string describing the drink action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " drinks from Puddle";
    }

    /**
     * Only provide the drink action when the actor is standing on this puddle.
     *
     * @param actor the actor interacting with the ground
     * @param location the location of this ground
     * @param direction direction label supplied by the engine
     * @return allowable actions for this ground
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        if (direction.isEmpty()) {
            actions.add(new ConsumeAction(this));
        }

        return actions;
    }

    /**
     * Returns whether this puddle has been sterilised (safe to drink).
     *
     * @return {@code true} if sterilised
     */
    @Override
    public boolean isSterilised() {
        return sterilised;
    }

    /**
     * Marks this puddle as sterilised so that future drinking heals rather than poisons.
     */
    @Override
    public void sterilise() {
        this.sterilised = true;
    }
}