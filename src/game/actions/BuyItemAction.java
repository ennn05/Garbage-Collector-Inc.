package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.economy.Wallet;
import game.interfaces.Purchasable;

/**
 * An action that allows an actor to buy a purchasable item from the Supercomputer.
 */
public class BuyItemAction extends TransactionAction {
    private final Purchasable purchasable;
    private final Location terminalLocation;

    /**
     * Constructor.
     *
     * @param purchasable the purchasable item option
     * @param terminalLocation the location of the Supercomputer
     */
    public BuyItemAction(Purchasable purchasable, Location terminalLocation) {
        this.purchasable = purchasable;
        this.terminalLocation = terminalLocation;
    }

    /**
     * Execute the purchase.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return result description
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!hasWallet(actor)) {
            return noWalletMessage(actor);
        }

        Wallet wallet = getWallet(actor);
        int price = purchasable.getPurchasePrice();

        if (!wallet.canAfford(price)) {
            return purchasable.onInsufficientCredits(actor, map);
        }

        int deductedCredits = wallet.deductCredits(price);
        Item purchasedItem = purchasable.createPurchasedItem();

        if (!actor.getInventory().add(purchasedItem)) {
            wallet.addCredits(deductedCredits);
            return actor + " cannot carry " + purchasedItem
                    + ". The transaction is cancelled and the credits are refunded.";
        }

        String result = actor + " buys " + purchasedItem + " for " + price + " credits.";

        String purchaseEffect = purchasable.onPurchased(actor, map, terminalLocation, wallet);
        if (!purchaseEffect.isEmpty()) {
            result += "\n" + purchaseEffect;
        }

        if (!actor.isConscious()) {
            result += "\n" + actor.unconscious(map);
        }

        result += "\nCurrent credits: " + wallet.getCredits() + ".";
        return result;
    }

    /**
     * Menu description.
     *
     * @param actor The actor performing the action.
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " buys " + purchasable + " for "
                + purchasable.getPurchasePrice() + " credits";
    }
}