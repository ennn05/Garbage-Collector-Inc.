package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BuyItemAction;
import game.actions.DepositItemAction;
import game.actions.SellItemAction;
import game.economy.QuotaSystem;
import game.economy.WalletHolder;
import game.enums.AccessLevel;
import game.interfaces.Depositable;
import game.interfaces.Purchasable;
import game.interfaces.QuotaHost;
import game.interfaces.Sellable;
import game.items.AccessCard;
import game.items.FirstAidKit;
import game.items.PlasmaCutter;
import game.items.SterilisationBox;

import java.util.ArrayList;
import java.util.List;

/**
 * A Supercomputer terminal that allows workers to buy and sell items.
 * Also manages the company quota system.
 */
public class Supercomputer extends Ground implements QuotaHost {
    private final List<Purchasable> purchasableItems;
    private QuotaSystem quotaSystem;

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
        purchasableItems.add(new PlasmaCutter());
    }

    /**
     * Initialise the quota system for this supercomputer.
     * Called after the supercomputer is placed on the map.
     *
     * @param location the location of this Supercomputer
     */
    public void initialiseQuotaSystem(Location location) {
        this.quotaSystem = new QuotaSystem(location);
    }

    /**
     * Get the quota system.
     *
     * @return the quota system
     */
    public QuotaSystem getQuotaSystem() {
        return quotaSystem;
    }

    /**
     * Update the quota system each turn.
     *
     * @param location the location of this Supercomputer
     */
    @Override
    public void tick(Location location) {
        if (quotaSystem != null) {
            String quotaUpdate = quotaSystem.tick(location.map());
            if (!quotaUpdate.isEmpty()) {
                System.out.println(quotaUpdate);
            }
        }
    }

    /**
     * Return buying, selling, and deposit actions when an actor interacts with the Supercomputer.
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

        // Add purchase actions
        for (Purchasable purchasable : purchasableItems) {
            actions.add(new BuyItemAction(purchasable, location));
        }

        // Add sell and deposit actions for items in inventory
        for (Item item : actor.getInventory().getItems()) {
            item.asCapability(Sellable.class).ifPresent(sellable ->
                    actions.add(new SellItemAction(item, sellable, location))
            );
            
            item.asCapability(Depositable.class).ifPresent(depositable ->
                    actions.add(new DepositItemAction(item, depositable, location))
            );
        }

        return actions;
    }
}