package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.MalletAction;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.grounds.CrackableGround;
import game.interfaces.Purchasable;
import game.interfaces.Resonator;

import java.util.ArrayList;
import java.util.List;

/**
 * A melee weapon that delivers a shockwave to an adjacent tile, degrading ground and pushing items.
 * <p>
 * The ResonanceMallet targets a single adjacent tile (North/South/East/West). Its shockwave radius
 * is {@value #SHOCKWAVE_RADIUS}, meaning only the struck tile is affected — no surrounding spread.
 * After each use the mallet enters a {@value #COOLDOWN}-turn cooldown before it can be used again.
 * The cooldown counter ticks up each turn while the item is in the holder's inventory.
 * Purchase price: {@value #PURCHASE_PRICE} credits. Weight: {@value #WEIGHT}.
 */
public class ResonanceMallet extends Item implements Purchasable, Resonator {
    private static final int COOLDOWN = 10;
    private static final int PURCHASE_PRICE = 50;
    private static final int SHOCKWAVE_POWER = 1;
    private static final int SHOCKWAVE_RADIUS = 0;
    private static final int WEIGHT = 5;

    /**
     * Creates a new ResonanceMallet item (display character: {@code p}).
     */
    public ResonanceMallet() {
        super("Resonance Mallet", 'p');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.COOLDOWN, new BaseStatistic(COOLDOWN));
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Provides {@link game.actions.MalletAction} options for each cardinal direction
     * when the mallet is not on cooldown. Returns an empty list while cooling down.
     *
     * @param owner the actor holding this item
     * @param map   the current game map
     * @return actions to strike North, South, East, or West, or an empty list if on cooldown
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        if (!isOnCooldown()) {
            ActionList actionList = new ActionList();
            Location here = map.locationOf(owner);
            addStrikeAction(actionList, map, here.x(), here.y() - 1, "North");
            addStrikeAction(actionList, map, here.x(), here.y() + 1, "South");
            addStrikeAction(actionList, map, here.x() - 1, here.y(), "West");
            addStrikeAction(actionList, map, here.x() + 1, here.y(), "East");
            return actionList;
        }
        return new ActionList();
    }

    private void addStrikeAction(ActionList actions, GameMap map, int x, int y, String direction) {
        Location target = locationAtOrNull(map, x, y);
        if (target != null) {
            actions.add(new MalletAction(target, direction, this));
        }
    }

    private Location locationAtOrNull(GameMap map, int x, int y) {
        try {
            return map.at(x, y);
        } catch (IndexOutOfBoundsException exception) {
            return null;
        }
    }

    /**
     * Resets the cooldown counter to zero, preventing use until the counter
     * reaches {@value #COOLDOWN} again via {@link #tick}.
     */
    public void applyCooldown() {
        this.modifyStatistic(ItemStatistics.COOLDOWN, StatisticOperations.UPDATE, 0);
    }

    /**
     * {@inheritDoc}
     *
     * @return a new ResonanceMallet instance
     */
    @Override
    public ResonanceMallet createPurchasedItem() {
        return new ResonanceMallet();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@value #PURCHASE_PRICE}
     */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@value #SHOCKWAVE_RADIUS} (epicenter only — no surrounding spread)
     */
    @Override
    public int getShockwaveRadius() {
        return SHOCKWAVE_RADIUS;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@value #SHOCKWAVE_POWER}
     */
    @Override
    public int getShockwavePower() {
        return SHOCKWAVE_POWER;
    }

    /**
     * Returns {@code true} if the mallet's cooldown counter has not yet reached
     * {@value #COOLDOWN} since its last use.
     *
     * @return {@code true} while cooling down, {@code false} when ready to use
     */
    public boolean isOnCooldown() {
        return this.getStatistic(ItemStatistics.COOLDOWN) < COOLDOWN;
    }

    /**
     * {@inheritDoc}
     *
     * @param buyer the actor attempting to buy this item
     * @param map   the map where the transaction happens
     * @return a message explaining the buyer cannot afford the item
     */
    @Override
    public String onInsufficientCredits(Actor buyer, GameMap map) {
        String message = buyer + " cannot afford the Resonance Mallet (costs " + PURCHASE_PRICE + " credits).";
        System.out.println(message);
        return message;
    }

    /**
     * {@inheritDoc}
     *
     * @param buyer            the actor purchasing this item
     * @param map              the game map
     * @param terminalLocation the location of the shop terminal
     * @param wallet           the buyer's wallet
     * @return a confirmation message
     */
    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        return buyer + " purchased the Resonance Mallet";
    }

    /**
     * Advances the cooldown counter by 1 each turn while the item is in an actor's inventory
     * and is still on cooldown.
     *
     * @param currentLocation the location of the actor holding this item
     * @param actor           the actor holding this item
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        if (isOnCooldown()) {
            this.modifyStatistic(ItemStatistics.COOLDOWN, StatisticOperations.INCREASE, 1);
        }
    }

    /**
     * Returns the display string including the current cooldown progress.
     *
     * @return name with cooldown status appended
     */
    @Override
    public String toString() {
        return super.toString() +
                " (Cooldown: " + this.getStatistic(ItemStatistics.COOLDOWN) + "/" + COOLDOWN + ")";
    }

    /**
     * Triggers the mallet's shockwave at the struck {@code epicenter} tile only
     * (radius 0). Pushes items at that location outward and degrades any
     * {@link game.grounds.CrackableGround} on the tile.
     *
     * @param epicenter the tile struck by the mallet
     * @param map       the game map
     */
    @Override
    public void triggerShockwave(Location epicenter, GameMap map) {
        if (!epicenter.getItems().isEmpty()) {
            List<Item> items = new ArrayList<>(epicenter.getItems());
            for (Item item : items) {
                Location target = findOneTilePushTarget(epicenter, epicenter, map);
                if (target != null) {
                    epicenter.removeItem(item);
                    target.addItem(item);
                }
            }
        }
        CrackableGround ground = epicenter.getGroundAs(CrackableGround.class);
        if (ground != null) {
            epicenter.setGround(ground.degrade());
        }
    }
}
