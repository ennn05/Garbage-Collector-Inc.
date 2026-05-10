package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.SteriliseAction;
import game.economy.Wallet;
import game.enums.Ability;
import game.enums.ItemStatistics;
import game.interfaces.Purchasable;
import game.interfaces.Sterilisable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A portable sterilisation box used to sterilise suitable targets.
 */
public class SterilisationBox extends Item implements Purchasable {
    private static final int WEIGHT = 7;
    private static final int PURCHASE_PRICE = 750;

    private final Random random = new Random();

    public SterilisationBox() {
        super("Sterilisation Box", '▣');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.enableAbility(Ability.STERILISING);
    }

    /**
     * Provide sterilise actions for sterilisable consumables that the actor is carrying,
     * items on the current location, and the ground on the current location.
     *
     * @param owner the actor carrying the item
     * @param map the map where the actor is
     * @return allowable actions for this item while being carried
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        Location currentLocation = map.locationOf(owner);

        for (Item item : owner.getInventory().getItems()) {
            item.asCapability(Sterilisable.class).ifPresent(sterilisable -> {
                if (!sterilisable.isSterilised()) {
                    actions.add(new SteriliseAction(sterilisable));
                }
            });
        }

        for (Item item : currentLocation.getItems()) {
            item.asCapability(Sterilisable.class).ifPresent(sterilisable -> {
                if (!sterilisable.isSterilised()) {
                    actions.add(new SteriliseAction(sterilisable));
                }
            });
        }

        Sterilisable groundTarget = currentLocation.getGroundAs(Sterilisable.class);
        if (groundTarget != null && !groundTarget.isSterilised()) {
            actions.add(new SteriliseAction(groundTarget));
        }

        return actions;
    }

    /**
     * Get the purchase price of the sterilisation box.
     *
     * @return purchase price in credits
     */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * Create a new sterilisation box after purchase.
     *
     * @return purchased sterilisation box
     */
    @Override
    public Item createPurchasedItem() {
        return new SterilisationBox();
    }

    /**
     * Apply the successful purchase effect.
     * One random item from the buyer's inventory is permanently erased.
     *
     * @param buyer the actor buying this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the buyer's wallet
     * @return result description
     */
    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        List<Item> carriedItems = new ArrayList<>(buyer.getInventory().getItems());

        if (carriedItems.isEmpty()) {
            return "The Sterilisation Box emits radiation, but there is no item to erase.";
        }

        Item erasedItem = carriedItems.get(random.nextInt(carriedItems.size()));
        buyer.getInventory().remove(erasedItem);

        return "The Sterilisation Box emits intense radiation and permanently erases "
                + erasedItem + " from " + buyer + "'s inventory.";
    }

    /**
     * Apply the failed purchase effect.
     * Sterilisation Box has no special insufficient-credit punishment.
     *
     * @param buyer the actor attempting to buy this item
     * @param map the map where the transaction happens
     * @return result description
     */
    @Override
    public String onInsufficientCredits(Actor buyer, GameMap map) {
        return buyer + " does not have enough credits to buy a Sterilisation Box.";
    }
}