package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.economy.QuotaSystem;
import game.grounds.Supercomputer;
import game.interfaces.Depositable;
import game.items.AlienArtifact;

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

        // Apply deposit effect
        String depositEffect = depositable.onDeposit(actor, supercomputerLocation);

        // Handle special case for Alien Artifact teleportation
        String result = depositEffect;
        if (itemToDeposit instanceof AlienArtifact) {
            // Find a random valid location on the map
            Location randomLocation = teleportToRandomLocation(actor, map);
            if (randomLocation != null && randomLocation != map.locationOf(actor)) {
                map.moveActor(actor, randomLocation);
                result += "\n" + actor + " is teleported to a new location!";
            }
        }

        // Update quota system
        Supercomputer supercomputer = (Supercomputer) supercomputerLocation.getGround();
        if (supercomputer != null) {
            QuotaSystem quotaSystem = supercomputer.getQuotaSystem();
            if (quotaSystem != null) {
                quotaSystem.progressQuota(companyCreditsReward);
                result += "\n[Deposited " + itemToDeposit.toString() + " for " + companyCreditsReward + 
                        " Company Credits. " + quotaSystem.getQuotaStatus() + "]";
            }
        }

        return result;
    }

    /**
     * Teleport actor to a random valid location on the map.
     *
     * @param actor the actor to teleport
     * @param map the game map
     * @return a random valid location, or null if none found
     */
    private Location teleportToRandomLocation(Actor actor, GameMap map) {
        // This is a simplified version - in a full implementation you'd want to
        // iterate through all locations and find valid ones
        Location currentLocation = map.locationOf(actor);
        int attempts = 0;
        Location newLocation = currentLocation;

        while (attempts < 100 && newLocation == currentLocation) {
            newLocation = map.at((int)(Math.random() * map.getXRange().max()), 
                                 (int)(Math.random() * map.getYRange().max()));
            attempts++;
        }

        return newLocation;
    }

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
