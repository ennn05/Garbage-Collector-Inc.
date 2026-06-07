package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.economy.Wallet;
import game.enums.AccessLevel;
import game.enums.ItemStatistics;
import game.interfaces.DoorUnlocker;
import game.interfaces.Purchasable;
import game.interfaces.Unlockable;

import java.util.Random;

/**
 * A portable access card with a security clearance level.
 */
public class AccessCard extends Item implements Purchasable, DoorUnlocker {
    private static final int LEVEL_1_PRICE = 50;
    private static final int LEVEL_2_PRICE = 100;
    private static final int LEVEL_3_PRICE = 200;

    private static final int LEVEL_2_DAMAGE = 5;
    private static final int LEVEL_3_HIDDEN_FEE = 50;
    private static final int LEVEL_3_HIDDEN_FEE_CHANCE_PERCENT = 50;

    private final AccessLevel level;
    private final Random random = new Random();

    /**
     * Create a level 1 access card by default.
     */
    public AccessCard() {
        this(AccessLevel.LEVEL_1);
    }

    /**
     * Create an access card with the given access level.
     *
     * @param level the clearance level of the card
     */
    public AccessCard(AccessLevel level) {
        super("Access Card " + level.getLabel(), level.getDisplayChar());
        this.level = level;
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(level.getWeight()));
    }

    /**
     * Get the clearance level of this card.
     *
     * @return the access level
     */
    public AccessLevel getLevel() {
        return level;
    }

    /**
     * Check whether this card can unlock the given target.
     *
     * @param unlockable the target to be unlocked
     * @return true if the target is locked and this card has enough clearance
     */
    @Override
    public boolean canUnlock(Unlockable unlockable) {
        return unlockable != null
                && !unlockable.isUnlocked()
                && level.ordinal() >= unlockable.getRequiredClearance().ordinal();
    }

    /**
     * Unlock the given target.
     *
     * @param unlockable the target to be unlocked
     */
    @Override
    public void unlock(Unlockable unlockable) {
        if (canUnlock(unlockable)) {
            unlockable.unlock();
        }
    }

    /**
     * Get the purchase price based on the access card level.
     *
     * @return purchase price in credits
     */
    @Override
    public int getPurchasePrice() {
        if (level == AccessLevel.LEVEL_1) {
            return LEVEL_1_PRICE;
        }

        if (level == AccessLevel.LEVEL_2) {
            return LEVEL_2_PRICE;
        }

        return LEVEL_3_PRICE;
    }

    /**
     * Create a new access card after purchase.
     *
     * @return purchased access card
     */
    @Override
    public Item createPurchasedItem() {
        return new AccessCard(level);
    }

    /**
     * Apply the successful purchase effect based on the access card level.
     *
     * @param buyer the actor buying this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the buyer's wallet
     * @return result description
     */
    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        if (level == AccessLevel.LEVEL_2) {
            buyer.hurt(LEVEL_2_DAMAGE);
            return "The Supercomputer calibrates the Level 2 Access Card by extracting a blood sample, "
                    + "dealing " + LEVEL_2_DAMAGE + " damage to " + buyer + ".";
        }

        if (level == AccessLevel.LEVEL_3) {
            if (random.nextInt(100) < LEVEL_3_HIDDEN_FEE_CHANCE_PERCENT) {
                int deductedCredits = wallet.deductCredits(LEVEL_3_HIDDEN_FEE);

                return "The Supercomputer applies a hidden fee of " + LEVEL_3_HIDDEN_FEE
                        + " credits for the Level 3 Access Card."
                        + "\nActual hidden fee deducted: " + deductedCredits + " credits.";
            }

            return "No hidden fee is applied for the Level 3 Access Card.";
        }

        return "";
    }

    /**
     * Apply the failed purchase effect.
     * Access Cards have no special insufficient-credit punishment.
     *
     * @param buyer the actor attempting to buy this item
     * @param map the map where the transaction happens
     * @return result description
     */
    @Override
    public String onInsufficientCredits(Actor buyer, GameMap map) {
        return buyer + " does not have enough credits to buy " + this + ".";
    }
}