package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.economy.QuotaSystem;
import game.interfaces.Depositable;
import game.interfaces.QuotaHost;

/**
 * An action that allows an actor to deposit a depositable item at the supercomputer.
 * This action updates the company quota and applies deposit effects.
 */
public class DepositItemAction extends Action {
    private final Item itemToDeposit;
    private final Depositable depositable;
    private final Location supercomputerLocation;

    /**
     * Constructor.
     *
     * @param itemToDeposit the item being deposited
     * @param depositable the depositable behaviour of the item
     * @param supercomputerLocation the location of the Supercomputer
     */
    public DepositItemAction(Item itemToDeposit, Depositable depositable, Location supercomputerLocation) {
        this.itemToDeposit = itemToDeposit;
        this.depositable = depositable;
        this.supercomputerLocation = supercomputerLocation;
    }

    /**
     * Execute the deposit.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return result description
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Check if actor is still carrying the item
        if (!actor.getInventory().getItems().contains(itemToDeposit)) {
            return actor + " is no longer carrying " + itemToDeposit + ".";
        }

        // Check if item can be deposited
        if (!depositable.canBeDeposited(actor)) {
            return actor + " cannot deposit " + itemToDeposit + ".";
        }

        // Get company credits reward
        int companyCreditsReward = depositable.getDepositReward();

        // Remove item from inventory
        if (!actor.getInventory().remove(itemToDeposit)) {
            return actor + " fails to deposit " + itemToDeposit + ".";
        }

        // Apply deposit effect (teleportation for AlienArtifact is handled inside onDeposit)
        String result = depositable.onDeposit(actor, supercomputerLocation);

        // Update quota system via QuotaHost interface — no concrete cast needed
        QuotaHost quotaHost = supercomputerLocation.getGroundAs(QuotaHost.class);
        if (quotaHost != null) {
            QuotaSystem quotaSystem = quotaHost.getQuotaSystem();
            if (quotaSystem != null) {
                quotaSystem.progressQuota(companyCreditsReward);
                result += "\n[Deposited " + itemToDeposit + " for " + companyCreditsReward +
                        " Company Credits. " + quotaSystem.getQuotaStatus() + "]";
            }
        }

        return result;
    }

    /**
     * Teleport actor to a random valid location on the map.
     * Validates that the location is walkable and can accept the actor.
     *
     * @param actor the actor to teleport
     * @param map the game map
     * @return a random valid location, or null if none found
     */
    /**
     * Menu description.
     *
     * @param actor The actor performing the action.
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " deposits " + itemToDeposit + " for " + depositable.getDepositReward() + " company credits";
    }
}
