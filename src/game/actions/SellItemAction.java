package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.economy.Wallet;
import game.interfaces.Sellable;

/**
 * An action that allows an actor to sell one sellable item to the Supercomputer.
 */
public class SellItemAction extends TransactionAction {
    private final Item item;
    private final Sellable sellable;
    private final Location terminalLocation;

    /**
     * Constructor.
     *
     * @param item the item being sold
     * @param sellable the sellable behaviour of the item
     * @param terminalLocation the location of the Supercomputer
     */
    public SellItemAction(Item item, Sellable sellable, Location terminalLocation) {
        this.item = item;
        this.sellable = sellable;
        this.terminalLocation = terminalLocation;
    }

    /**
     * Execute the sale.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return result description
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Wallet wallet = getWallet(actor);

        if (wallet == null) {
            return noWalletMessage(actor);
        }

        if (!actor.getInventory().getItems().contains(item)) {
            return actor + " is no longer carrying " + item + ".";
        }

        int sellingPrice = sellable.getSellPrice();

        if (!actor.getInventory().remove(item)) {
            return actor + " fails to sell " + item + ".";
        }

        int creditsAdded = wallet.addCredits(sellingPrice);

        String result = actor + " sells " + item + " for " + sellingPrice + " credits.";
        if (creditsAdded < sellingPrice) {
            result += "\nOnly " + creditsAdded + " credits are stored because the wallet cannot exceed "
                    + Wallet.MAX_CREDITS + " credits.";
        }

        String saleEffect = sellable.onSold(actor, map, terminalLocation, wallet);
        if (!saleEffect.isEmpty()) {
            result += "\n" + saleEffect;
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
        return actor + " sells " + item + " for " + sellable.getSellPrice() + " credits";
    }
}