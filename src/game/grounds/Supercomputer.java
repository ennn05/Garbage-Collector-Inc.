package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BuyItemAction;
import game.actions.SellItemAction;
import game.economy.WalletHolder;
import game.enums.AccessLevel;
import game.interfaces.Purchasable;
import game.interfaces.Sellable;
import game.items.AccessCard;
import game.items.FirstAidKit;
import game.items.SterilisationBox;

import java.util.ArrayList;
import java.util.List;

/**
 * A Supercomputer terminal that allows workers to buy and sell items.
 */
public class Supercomputer extends Ground {
    private final List<Purchasable> purchasableItems;

    /**
     * Constructor.
     */
    public Supercomputer() {
        super('≡', "Supercomputer");
        this.purchasableItems = new ArrayList<>();

        purchasableItems.add(new FirstAidKit());
        purchasableItems.add(new SterilisationBox());
        purchasableItems.add(new AccessCard(AccessLevel.LEVEL_1));
        purchasableItems.add(new AccessCard(AccessLevel.LEVEL_2));
        purchasableItems.add(new AccessCard(AccessLevel.LEVEL_3));
    }

    /**
     * Return buying and selling actions when an actor interacts with the Supercomputer.
     *
     * @param actor the Actor acting
     * @param location the location of this Supercomputer
     * @param direction the direction of the Supercomputer from the Actor
     * @return actions available from this Supercomputer
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        WalletHolder walletHolder = actor.asCapability(WalletHolder.class).orElse(null);
        if (walletHolder == null) {
            return actions;
        }

        for (Purchasable purchasable : purchasableItems) {
            actions.add(new BuyItemAction(purchasable, location));
        }

        for (Item item : actor.getInventory().getItems()) {
            item.asCapability(Sellable.class).ifPresent(sellable ->
                    actions.add(new SellItemAction(item, sellable, location))
            );
        }

        return actions;
    }
}